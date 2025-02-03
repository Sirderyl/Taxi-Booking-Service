package uk.ac.newcastle.enterprisemiddleware.taxi;

import javax.validation.constraints.NotNull;

public class TaxiDTO {

    @NotNull
    private String registration;

    @NotNull
    private int numberOfSeats;

    public @NotNull String getRegistration() {
        return registration;
    }

    public void setRegistration(@NotNull String registration) {
        this.registration = registration;
    }

    @NotNull
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(@NotNull int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxiDTO)) return false;

        TaxiDTO taxiDTO = (TaxiDTO) o;
        return getNumberOfSeats() == taxiDTO.getNumberOfSeats() && getRegistration().equals(taxiDTO.getRegistration());
    }

    @Override
    public int hashCode() {
        int result = getRegistration().hashCode();
        result = 31 * result + getNumberOfSeats();
        return result;
    }

    @Override
    public String toString() {
        return "TaxiDTO{" +
                "registration='" + registration + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }
}
