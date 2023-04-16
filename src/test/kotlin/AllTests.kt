import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(GetUsersTests::class,
    GetResourcesTests::class,
    PostRegisterTests::class,
    PutUsersTests::class,
    PatchUsersTests::class,
    DeleteUserTests::class)
@SelectPackages("exercise")
class TestAllSelectPackage {
}
