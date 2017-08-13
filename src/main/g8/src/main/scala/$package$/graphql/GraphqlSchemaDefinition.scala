package $package$.graphql

import $package$.services.{AuthService, DummyService}
import sangria.schema._

import scala.concurrent.ExecutionContext

case class GraphQLContextServices(authService: AuthService, dummyService: DummyService)(implicit executionContext: ExecutionContext)
case class GraphQLContext(credentials: Option[Map[String, AnyRef]], services: GraphQLContextServices)

object GraphqlSchemaDefinition {

  val Query = ObjectType(
    "Query", fields[GraphQLContext, Unit](
      DummyService.graphqlFields
    ))

  val Mutation = ObjectType(
    "Mutation", fields[GraphQLContext, Unit](
      DummyService.graphqlMutationsAddDummy
    )
  )

  val apiSchema = Schema(Query, Some(Mutation))
}
