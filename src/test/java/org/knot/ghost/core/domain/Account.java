package org.knot.ghost.core.domain;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = 8751282105532159742L;

    private String            username;
    private String            password;
    private String            email;
    private String            firstName;
    private String            lastName;
    private String            status;
    private String            address1;
    private String            address2;
    private String            city;
    private String  state;
    private String  zip;
    private String  country;
    private String  phone;
    private String  favouriteCategoryId;
    private String  languagePreference;
    private boolean listOption;
    private boolean bannerOption;
    private String  bannerName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFavouriteCategoryId() {
        return favouriteCategoryId;
    }

    public void setFavouriteCategoryId(String favouriteCategoryId) {
        this.favouriteCategoryId = favouriteCategoryId;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public boolean isListOption() {
        return listOption;
    }

    public void setListOption(boolean listOption) {
        this.listOption = listOption;
    }

    public boolean isBannerOption() {
        return bannerOption;
    }

    public void setBannerOption(boolean bannerOption) {
        this.bannerOption = bannerOption;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    @Override
    public String toString() {
        return "Account [address1=" + address1 + ", address2=" + address2 + ", bannerName=" + bannerName + ", bannerOption=" + bannerOption
               + ", city=" + city + ", country=" + country + ", email=" + email + ", favouriteCategoryId=" + favouriteCategoryId
               + ", firstName=" + firstName + ", languagePreference=" + languagePreference + ", lastName=" + lastName + ", listOption="
               + listOption + ", password=" + password + ", phone=" + phone + ", state=" + state + ", status=" + status + ", username="
               + username + ", zip=" + zip + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address1 == null) ? 0 : address1.hashCode());
        result = prime * result + ((address2 == null) ? 0 : address2.hashCode());
        result = prime * result + ((bannerName == null) ? 0 : bannerName.hashCode());
        result = prime * result + (bannerOption ? 1231 : 1237);
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((favouriteCategoryId == null) ? 0 : favouriteCategoryId.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((languagePreference == null) ? 0 : languagePreference.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + (listOption ? 1231 : 1237);
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((zip == null) ? 0 : zip.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Account other = (Account) obj;
        if (address1 == null) {
            if (other.address1 != null) return false;
        } else if (!address1.equals(other.address1)) return false;
        if (address2 == null) {
            if (other.address2 != null) return false;
        } else if (!address2.equals(other.address2)) return false;
        if (bannerName == null) {
            if (other.bannerName != null) return false;
        } else if (!bannerName.equals(other.bannerName)) return false;
        if (bannerOption != other.bannerOption) return false;
        if (city == null) {
            if (other.city != null) return false;
        } else if (!city.equals(other.city)) return false;
        if (country == null) {
            if (other.country != null) return false;
        } else if (!country.equals(other.country)) return false;
        if (email == null) {
            if (other.email != null) return false;
        } else if (!email.equals(other.email)) return false;
        if (favouriteCategoryId == null) {
            if (other.favouriteCategoryId != null) return false;
        } else if (!favouriteCategoryId.equals(other.favouriteCategoryId)) return false;
        if (firstName == null) {
            if (other.firstName != null) return false;
        } else if (!firstName.equals(other.firstName)) return false;
        if (languagePreference == null) {
            if (other.languagePreference != null) return false;
        } else if (!languagePreference.equals(other.languagePreference)) return false;
        if (lastName == null) {
            if (other.lastName != null) return false;
        } else if (!lastName.equals(other.lastName)) return false;
        if (listOption != other.listOption) return false;
        if (password == null) {
            if (other.password != null) return false;
        } else if (!password.equals(other.password)) return false;
        if (phone == null) {
            if (other.phone != null) return false;
        } else if (!phone.equals(other.phone)) return false;
        if (state == null) {
            if (other.state != null) return false;
        } else if (!state.equals(other.state)) return false;
        if (status == null) {
            if (other.status != null) return false;
        } else if (!status.equals(other.status)) return false;
        if (username == null) {
            if (other.username != null) return false;
        } else if (!username.equals(other.username)) return false;
        if (zip == null) {
            if (other.zip != null) return false;
        } else if (!zip.equals(other.zip)) return false;
        return true;
    }
}
