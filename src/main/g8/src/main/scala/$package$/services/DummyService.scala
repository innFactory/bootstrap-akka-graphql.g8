package $package$.services

import $package$.graphql.GraphQLContext
import $package$.models.Dummy
import $package$.models.db.DummyRepository
import $package$.utils.Persistence
import sangria.macros.derive._
import sangria.schema.{Args, Argument, Field, ListType, LongType, OptionInputType, OptionType, StringType, fields}

import scala.concurrent.ExecutionContext

class DummyService()(implicit executionContext: ExecutionContext)  extends Persistence {
  val dummyRepository = new DummyRepository()

  def findGraphQL(args : Args) = {
    executeOperation {
      dummyRepository.find(args.argOpt("id"), args.argOpt("dummy"))
    }
  }

  @GraphQLField
  def addDummy(dummy: String) = {
    val d = Dummy(None, dummy)
    executeOperation {
      dummyRepository.save(d)
    }
  }
}

object DummyService {
  implicit val graphqlType = deriveObjectType[DummyService, Dummy]()

  val graphqlFields = fields[GraphQLContext, Unit](
    Field("dummy", ListType(graphqlType),
      arguments = Argument("id", OptionInputType(LongType), description = "id of the dummy") :: Nil,
      resolve = f => f.ctx.services.dummyService.findGraphQL(f.args)

    )).head

  val graphqlMutationsAddDummy = fields[GraphQLContext, Unit](Field("addDummy", OptionType(DummyService.graphqlType),
    arguments = Argument("dummy", StringType) :: Nil,
    resolve = f => f.ctx.services.dummyService.addDummy(f.arg("dummy"))
  )).head

}