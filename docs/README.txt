Dynamic Dao is ORM framework. It allows to use annotations to attach SQL execution to interface methods.

It takes care of 3 things:

1. Binds method arguments to query parameters
2. Executes SQL statement
3. Maps statement's result set to method's return type.

From developer's prospective it feels like instead of writing method body you just write SQL statement. Here is example:

JNDIDao("db/testdb")
public interface TestDao {
  @Select("select id, name from users where id = #1#")
  UserBean getUser(int id);
}

here is the code that illustrates how to use this dao:

ALinker factory = new ALinker();
TestDao dao = factory.create(TestDao.class);
UserBean res = dao.getUser(1);

For more examples check out unit tests code in subdirectory test