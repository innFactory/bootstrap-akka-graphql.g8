package $package$.models

import com.byteslounge.slickrepo.meta.Entity
import sangria.macros.derive._

@GraphQLDescription(description = "Thats a dummy! Get id or a string...")
case class Dummy(override val id: Option[Long],
                 @GraphQLDeprecated(deprecationReason = "dummy val is old") dummy: String) extends Entity[Dummy, Long] {
  override def withId(id: Long): Dummy = this.copy(id = Some(id))
}
