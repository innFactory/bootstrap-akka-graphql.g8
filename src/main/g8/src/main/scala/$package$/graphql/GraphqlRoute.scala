package $package$.graphql

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import $package$.http.SecurityDirectives
import $package$.services.AuthService
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class GraphqlRoute(graphQLServices: GraphQLContextServices)(implicit executionContext: ExecutionContext, override protected val authService: AuthService) extends SecurityDirectives {
  // \$COVERAGE-OFF\$No changes required
  val route: Route =
    (post & path("graphql")) {
      authenticate { credentials =>
        entity(as[JsValue]) { requestJson ⇒
          val JsObject(fields) = requestJson

          val JsString(query) = fields("query")

          val operation = fields.get("operationName") collect {
            case JsString(op) ⇒ op
          }

          val vars = fields.get("variables") match {
            case Some(obj: JsObject) ⇒ obj
            case _ ⇒ JsObject.empty
          }

          val graphQLContext = GraphQLContext(Some(credentials), graphQLServices)

          QueryParser.parse(query) match {
            // query parsed successfully, time to execute it!
            case Success(queryAst) ⇒
              complete(Executor.execute(GraphqlSchemaDefinition.apiSchema, queryAst,
                variables = vars,
                operationName = operation,
                userContext = graphQLContext
              )
                .map(OK → _)
                .recover {
                  case error: QueryAnalysisError ⇒ BadRequest → error.resolveError
                  case error: ErrorWithResolver ⇒ InternalServerError → error.resolveError
                })

            // can't parse GraphQL query, return error
            case Failure(error) ⇒
              complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
          }


        }
      }
    } ~
      get {
        getFromResource("graphiql.html")
      }
  // \$COVERAGE-ON\$
}

