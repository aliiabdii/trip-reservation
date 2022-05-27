package org.aliabdi.trip.core.data

import org.aliabdi.trip.core.ReservationType
import org.hibernate.Hibernate
import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "reservation", indexes = [
        Index(name = "trip_idx", columnList = "trip_id"),
        Index(name = "customer_idx", columnList = "customer_id"),
        Index(name = "type_idx", columnList = "type")
    ]
)
data class Reservation (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    var trip: Trip,

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: Customer,

    @Column(name = "num_of_seats", nullable = false)
    var numberOfSeats: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ReservationType,

    @Column(name = "creation_date", nullable = false)
    var creationDate: Date? = null,
) {
    @PrePersist
    fun onCreate() {
        creationDate = Date()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Reservation

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , trip = $trip , customer = $customer , numberOfSeats = $numberOfSeats , type = $type , creationDate = $creationDate )"
    }

}