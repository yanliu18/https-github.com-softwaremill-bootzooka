import com.mongodb.Mongo
import java.util.concurrent.TimeUnit
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import pl.softwaremill.bootstrap.Beans
import pl.softwaremill.bootstrap.rest.{PasswordRecoveryServlet, UsersServlet, EntriesServlet, UptimeServlet}
import org.scalatra._
import javax.servlet.ServletContext

/**
 * This is the ScalatraBootstrap bootstrap file. You can use it to mount servlets or
 * filters. It's also a good place to put initialization code which needs to
 * run at application start (e.g. database configurations), and init params.
 */
class ScalatraBootstrap extends LifeCycle with Beans {
  val Prefix = "/rest"

  override def init(context: ServletContext) {
    MongoDB.defineDb(DefaultMongoIdentifier, new Mongo, "bootstrap")

    scheduler.scheduleAtFixedRate(emailSendingService, 60, 1, TimeUnit.SECONDS)

    context.mount(new EntriesServlet(entryService, userService), Prefix + "/entries")
    context.mount(new UptimeServlet, Prefix + "/uptime")
    context.mount(new UsersServlet(userService), Prefix + "/users")
    context.mount(new PasswordRecoveryServlet(passwordRecoveryService), Prefix + "/passwordrecovery")

    context.put("bootstrap", this)
  }


  override def destroy(context: ServletContext) {
    scheduler.shutdownNow()
  }

}