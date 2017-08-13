package $package$

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import $package$.http.HttpService
import $package$.graphql.GraphQLContextServices
import $package$.services.{AuthService, DummyService}
import $package$.utils.{AWSCognitoValidation, AutoValidate, Configuration, FlywayService}

import scala.concurrent.ExecutionContext

object Main extends App with Configuration {
  // \$COVERAGE-OFF\$Main Application Wrapper
  implicit val actorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val flywayService = new FlywayService(jdbcUrl, dbUser, dbPassword)
  flywayService.migrateDatabaseSchema

  implicit val authService = new AuthService(new AutoValidate)
  //implicit val authService = new AuthService(new AWSCognitoValidation(authCognito, log)) Use this Service for AWS ;-)

  val dummyService = new DummyService()
  val graphQLContextServices = GraphQLContextServices(authService, dummyService)
  val httpService = new HttpService(graphQLContextServices)
  Http().bindAndHandle(httpService.routes, httpHost, httpPort)
  // \$COVERAGE-ON\$
}
