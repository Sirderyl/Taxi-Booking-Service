package uk.ac.newcastle.enterprisemiddleware.hotel;

import java.time.OffsetDateTime;

public class HotelBookingGetDTO {

    private Long id;
    private OffsetDateTime hotelBookingDate;
    private Hotel hotel;
    private HotelCustomer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getHotelBookingDate() {
        return hotelBookingDate;
    }

    public void setHotelBookingDate(OffsetDateTime hotelBookingDate) {
        this.hotelBookingDate = hotelBookingDate;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public HotelCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(HotelCustomer customer) {
        this.customer = customer;
    }
}
