package org.aliabdi.trip.core.data

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "city")
data class City (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "lat", nullable = false)
    var latitude: Float,

    @Column(name = "lng", nullable = false)
    var longitude: Float
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as City

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , latitude = $latitude , longitude = $longitude )"
    }

}