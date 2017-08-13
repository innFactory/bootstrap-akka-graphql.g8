package $package$

import akka.http.scaladsl.testkit.ScalatestRouteTest
import $package$.graphql.GraphqlSchemaDefinition.apiSchema
import $package$.graphql.{GraphQLContext, GraphQLContextServices}
import $package$.http.HttpService
import $package$.services.{AuthService, DummyService}
import $package$.utils.AutoValidate
import $package$.utils.InMemoryPostgresStorage._
import org.scalatest.{Matchers, WordSpec}
import sangria.ast.Document
import sangria.execution.Executor
import sangria.marshalling.sprayJson._
import sangria.renderer.SchemaRenderer
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

trait BaseServiceTest extends WordSpec with Matchers with ScalatestRouteTest {

  println(SchemaRenderer.renderSchema(apiSchema))

  dbProcess.getProcessId

  val dummyService = new DummyService()
  implicit val authService = new AuthService(new AutoValidate)
  val graphQLContextServices = GraphQLContextServices(authService, dummyService)

  val httpService = new HttpService(graphQLContextServices)

  def executeQuery(query: Document, vars: JsObject = JsObject.empty) = {
    val futureResult = Executor.execute(apiSchema, query,
      variables = vars,
      userContext = GraphQLContext(None, graphQLContextServices)
      )
    Await.result(futureResult, 10.seconds)
  }
}
