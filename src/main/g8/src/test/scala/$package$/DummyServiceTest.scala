package $package$

import $package$.models.Dummy
import sangria.macros._
import spray.json._

class DummyServiceTest extends BaseServiceTest {


  "API Schema" should {
    "correctly identify dummy 1 as a" in {
      val query =
        graphql"""
          query{dummy(id:1) {
            id,
            dummy
          }
        }"""

      executeQuery(query) should be (
        """
        {
          "data": {
            "dummy": [
          {
            "id": 1,
            "dummy": "a"
          }
            ]
          }
        }
       """.parseJson)
    }

    "allow to add dummy e with id 5" in {
      Dummy(Some(5), "e") //should be created
      val query =
        graphql"""
        mutation {
          addDummy(dummy: "e") {
           id
           dummy
          }
        }
       """

      executeQuery(query) should be (
        """{
          "data": {
            "addDummy": {
            "id": 5,
            "dummy": "e"
          }
          }
        }"""
        .parseJson)
    }
  }

}


