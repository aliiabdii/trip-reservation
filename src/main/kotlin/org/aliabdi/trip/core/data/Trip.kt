package org.aliabdi.trip.core.data

import org.hibernate.Hibernate
import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "trip")
data class Trip (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "total_seats", nullable = false)
    var totalSeats: Int,

    @ManyToOne
    @JoinColumn(name = "from_city_id", nullable = false)
    var from: City,

    @ManyToOne
    @JoinColumn(name = "to_city_id", nullable = false)
    var to: City,

    @Column(name = "departure_time", nullable = true)
    var departureDate: Date,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Trip

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , totalSeats = $totalSeats , from = $from , to = $to , departureDate = $departureDate )"
    }

}