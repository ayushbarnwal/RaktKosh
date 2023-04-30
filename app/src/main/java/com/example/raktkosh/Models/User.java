package com.example.raktkosh.Models;

public class User {

    private String phoneNo;
    private String First_Name;
    private String Last_Name;
    private String Gender;
    private String Date_Of_Birth;
    private String Email_Address;
    private String Postal_Address;
    private String Blood_Group;
    private String userId;
    private String profilePic;
    private String City;
    private String State;
    private String Country;
    private String TypeRequested;
    private String haveReplacement;
    private String requestedBloodGroup;
    private String bloodUnit;
    private String hospitalName;
    private String requiredUpto;
    private String pin_Code;
    private String request_id;
    private String gotVerified;
    private String DonateUnit;
    private String request_accepted;
    private String unit_fulfilled;

    public String getDocument_pdf() {
        return document_pdf;
    }

    public void setDocument_pdf(String document_pdf) {
        this.document_pdf = document_pdf;
    }

    private String document_pdf;

    public String getPatient_situation() {
        return patient_situation;
    }

    public void setPatient_situation(String patient_situation) {
        this.patient_situation = patient_situation;
    }

    private String patient_situation;

    public String getSosRequested() {
        return SosRequested;
    }

    public void setSosRequested(String sosRequested) {
        SosRequested = sosRequested;
    }

    private String SosRequested;

    public String getDonation_request() {
        return donation_request;
    }

    public void setDonation_request(String donation_request) {
        this.donation_request = donation_request;
    }

    private String donation_request;

    public String getUnit_fulfilled() {
        return unit_fulfilled;
    }

    public void setUnit_fulfilled(String unit_fulfilled) {
        this.unit_fulfilled = unit_fulfilled;
    }

    public String getUser_accepted_request() {
        return user_accepted_request;
    }

    public void setUser_accepted_request(String user_accepted_request) {
        this.user_accepted_request = user_accepted_request;
    }

    private String user_accepted_request;

    public String getHasDonated() {
        return hasDonated;
    }

    public void setHasDonated(String hasDonated) {
        this.hasDonated = hasDonated;
    }

    private String hasDonated;

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    private String request_status;


    public User(){}

    public User(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public User(String phoneNo, String request_id, String SosRequested, String document_pdf){
        this.phoneNo = phoneNo;
        this.request_id = request_id;
        this.SosRequested = SosRequested;
        this.document_pdf = document_pdf;
    }

    public User(String userId, String DonateUnit, String hospitalName, String request_accepted, String request_status,String hasDonated, String user_accepted_request, String unit_fulfilled, String donation_request, String patient_situation){
        this.userId = userId;
        this.DonateUnit = DonateUnit;
        this.hospitalName = hospitalName;
        this.request_accepted = request_accepted;
        this.request_status = request_status;
        this.hasDonated = hasDonated;
        this.user_accepted_request = user_accepted_request;
        this.unit_fulfilled = unit_fulfilled;
        this.donation_request = donation_request;
        this.patient_situation = patient_situation;
    }

    public User(String first_Name, String last_Name, String gender, String date_Of_Birth,String blood_Group, String email_Address, String postal_Address, String userId, String profilePic, String City, String State, String Country, String pin_Code) {
        First_Name = first_Name;
        Last_Name = last_Name;
        Gender = gender;
        Date_Of_Birth = date_Of_Birth;
        Blood_Group = blood_Group;
        Email_Address = email_Address;
        Postal_Address = postal_Address;
        this.userId = userId;
        this.profilePic = profilePic;
        this.City = City;
        this.State = State;
        this.Country = Country;
        this.pin_Code = pin_Code;
    }

    public User(String first_Name, String last_Name, String TypeRequested, String haveReplacement, String blood_Group, String bloodUnit, String hospitalName, String phoneNo, String requiredUpto, String userId, String gotVerified){
        First_Name = first_Name;
        Last_Name = last_Name;
        this.TypeRequested = TypeRequested;
        this.haveReplacement = haveReplacement;
        Blood_Group = blood_Group;
        this.bloodUnit = bloodUnit;
        this.hospitalName = hospitalName;
        this.phoneNo = phoneNo;
        this.requiredUpto = requiredUpto;
        this.userId = userId;
        this.gotVerified = gotVerified;
    }

    public User(String first_Name, String last_Name, String TypeRequested, String haveReplacement, String blood_Group, String bloodUnit, String hospitalName, String phoneNo, String requiredUpto, String userId, String gotVerified, String request_id){
        First_Name = first_Name;
        Last_Name = last_Name;
        this.TypeRequested = TypeRequested;
        this.haveReplacement = haveReplacement;
        Blood_Group = blood_Group;
        this.bloodUnit = bloodUnit;
        this.hospitalName = hospitalName;
        this.phoneNo = phoneNo;
        this.requiredUpto = requiredUpto;
        this.userId = userId;
        this.gotVerified = gotVerified;
        this.request_id = request_id;
    }



    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getDate_Of_Birth() {
        return Date_Of_Birth;
    }

    public void setDate_Of_Birth(String date_Of_Birth) {
        Date_Of_Birth = date_Of_Birth;
    }

    public String getBlood_Group() {
        return Blood_Group;
    }

    public void setBlood_Group(String blood_Group) {
        Blood_Group = blood_Group;
    }

    public String getEmail_Address() {
        return Email_Address;
    }

    public void setEmail_Address(String email_Address) {
        Email_Address = email_Address;
    }

    public String getPostal_Address() {
        return Postal_Address;
    }

    public void setPostal_Address(String postal_Address) {
        Postal_Address = postal_Address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setCity(String city) {
        City = city;
    }
    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getPin_Code() {
        return pin_Code;
    }

    public void setPin_Code(String pin_Code) {
        this.pin_Code = pin_Code;
    }

    public String getTypeRequested() {
        return TypeRequested;
    }

    public void setTypeRequested(String typeRequested) {
        TypeRequested = typeRequested;
    }

    public String getHaveReplacement() {
        return haveReplacement;
    }

    public void setHaveReplacement(String haveReplacement) {
        this.haveReplacement = haveReplacement;
    }

    public String getRequestedBloodGroup() {
        return requestedBloodGroup;
    }

    public void setRequestedBloodGroup(String requestedBloodGroup) {
        this.requestedBloodGroup = requestedBloodGroup;
    }

    public String getBloodUnit() {
        return bloodUnit;
    }

    public void setBloodUnit(String bloodUnit) {
        this.bloodUnit = bloodUnit;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getRequiredUpto() {
        return requiredUpto;
    }

    public void setRequiredUpto(String requiredUpto) {
        this.requiredUpto = requiredUpto;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequest_accepted() {
        return request_accepted;
    }

    public void setRequest_accepted(String requested_accepted) {
        this.request_accepted = requested_accepted;
    }

    public String getDonateUnit() {
        return DonateUnit;
    }

    public void setDonateUnit(String donateUnit) {
        DonateUnit = donateUnit;
    }

    public String getGotVerified() {
        return gotVerified;
    }

    public void setGotVerified(String gotVerified) {
        this.gotVerified = gotVerified;
    }
}
