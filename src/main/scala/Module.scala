import com.google.inject.AbstractModule
import repositories.{Repository, RepositoryImpl}
import services.{LedgerService, LedgerServiceImpl}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Repository]).to(classOf[RepositoryImpl])
    bind(classOf[LedgerService]).to(classOf[LedgerServiceImpl])
  }
}
