package uk.ac.newcastle.enterprisemiddleware.hotel;

import java.time.LocalDate;

public class HotelBookingGetDTO2 {

    private Long id;
    private LocalDate hotelBookingDate;
    private Hotel hotel;
    private HotelCustomer customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getHotelBookingDate() {
        return hotelBookingDate;
    }

    public void setHotelBookingDate(LocalDate hotelBookingDate) {
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
