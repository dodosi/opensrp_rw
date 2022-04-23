package org.smartregister.anc.library.custom;

public class CustomClient {

    private String firstName;
    private String lastName;
    private String registrationId;
    private String age;
    private  String ga;
    private String nextContactDate;
    private int attentionFlag;
    private String baseIntityId;

    public CustomClient() {
    }

    public CustomClient(String firstName, String lastName, String registrationId, String age, String ga, String nextContactDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationId = registrationId;
        this.age = age;
        this.ga = ga;
        this.nextContactDate = nextContactDate;
    }

    public String getBaseIntityId() {
        return baseIntityId;
    }

    public void setBaseIntityId(String baseIntityId) {
        this.baseIntityId = baseIntityId;
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

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGa() {
        return ga;
    }

    public void setGa(String ga) {
        this.ga = ga;
    }

    public String getNextContactDate() {
        return nextContactDate;
    }

    public void setNextContactDate(String nextContactDate) {
        this.nextContactDate = nextContactDate;
    }

    public int getAttentionFlag() {
        return attentionFlag;
    }

    public void setAttentionFlag(int attentionFlag) {
        this.attentionFlag = attentionFlag;
    }

    @Override
    public String toString() {
        return "CustomClient{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", age='" + age + '\'' +
                ", ga='" + ga + '\'' +
                ", nextContactDate='" + nextContactDate + '\'' +
                '}';
    }
}
