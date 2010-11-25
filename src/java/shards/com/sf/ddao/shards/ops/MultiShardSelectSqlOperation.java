package com.sf.ddao.shards.ops;

import com.sf.ddao.alinker.ALinker;
import com.sf.ddao.alinker.initializer.InitializerException;
import com.sf.ddao.alinker.inject.Link;
import com.sf.ddao.chain.CtxHelper;
import com.sf.ddao.chain.MethodCallCtx;
import com.sf.ddao.conn.ConnectionHandlerHelper;
import com.sf.ddao.factory.StatementFactory;
import com.sf.ddao.factory.param.JoinListParameter;
import com.sf.ddao.ops.SelectSqlOperation;
import com.sf.ddao.shards.MultiShardResultMerger;
import com.sf.ddao.shards.MultiShardSelect;
import com.sf.ddao.shards.conn.ShardedConnectionHandler;
import org.apache.commons.chain.Context;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by psyrtsov
 */
public class MultiShardSelectSqlOperation extends SelectSqlOperation {
    public static final String KEY_LIST_CONTEXT_VALUE = "keyList";
    private final ALinker aLinker;
    private MultiShardResultMerger resultMerger;

    @Link
    public MultiShardSelectSqlOperation(ALinker aLinker, StatementFactory statementFactory) {
        super(statementFactory);
        this.aLinker = aLinker;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        ShardedConnectionHandler shardedConnectionHandler = CtxHelper.get(context, ShardedConnectionHandler.class);
        Map<DataSource, Collection<Object>> shardKeyListMap = shardedConnectionHandler.getShardKeyMapping(context);

        final MethodCallCtx callCtx = CtxHelper.get(context, MethodCallCtx.class);
        List<Object> resList = new ArrayList<Object>(shardKeyListMap.size());
        for (Map.Entry<DataSource, Collection<Object>> entry : shardKeyListMap.entrySet()) {
            DataSource ds = entry.getKey();
            Collection<Object> keys = entry.getValue();
            //noinspection unchecked
            context.put(KEY_LIST_CONTEXT_VALUE, JoinListParameter.join(keys));
            Connection oldConnection = ConnectionHandlerHelper.setConnection(context, ds.getConnection());
            assert oldConnection == null;
            super.execute(context);
            resList.add(callCtx.getLastReturn());
            ConnectionHandlerHelper.closeConnection(context);
        }

        @SuppressWarnings({"unchecked"})
        Object res = resultMerger.reduce(resList);
        callCtx.setLastReturn(res);
        return CONTINUE_PROCESSING;
    }

    @Override
    public void init(AnnotatedElement element, Annotation annotation) throws InitializerException {
        MultiShardSelect multiShardSelect = element.getAnnotation(MultiShardSelect.class);
        resultMerger = aLinker.create(multiShardSelect.resultMerger());
        super.init(element, multiShardSelect.value());
    }
}
