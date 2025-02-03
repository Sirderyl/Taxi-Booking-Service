package uk.ac.newcastle.enterprisemiddleware.hotel;

import java.io.Serializable;

public class Hotel implements Serializable {

    private Long id;
    private String name;
    private String phoneNumber;
    private String postalCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;

        Hotel hotel = (Hotel) o;
        return getId().equals(hotel.getId()) && getName().equals(hotel.getName()) && getPhoneNumber().equals(hotel.getPhoneNumber()) && getPostalCode().equals(hotel.getPostalCode());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        result = 31 * result + getPostalCode().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
