package com.hionstudios.zerroo.flow.user;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.CommonUtil;
import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.flow.ImageUtil;
import com.hionstudios.zerroo.model.KycVerificationStatus;
import com.hionstudios.zerroo.model.User;
import com.hionstudios.zerroo.model.UserHistory;
import com.hionstudios.security.NoPasswordEncoder;
import com.hionstudios.time.TimeUtil;

public class UserTransaction {

    public MapResponse profile() {
        long userid = UserUtil.getUserid();
        String sql;
        if (UserUtil.isDistributor()) {
            sql = "Select Users.Firstname, Users.Lastname, Users.Username, Users.Phone, Users.Email, Users.Gender, Users.Address_1, Users.Address_2, Users.Landmark, Users.Postcode, Users.City, Users.State, Users.Nominee_name, Users.Nominee_relation, To_Char(To_Timestamp(Users.Dob/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') Dob, Distributors.Email_Verified, Distributors.Phone_Verified, Kyc_Verification_Statuses.Status Kyc_Status, Distributors.Kyc_Rejection_Reason, Bank_Verification_Statuses.Status Bank_Status, Distributors.Bank_Rejection_Reason, Users.Avatar From Users Join Distributors On Distributors.Id = Users.Id Left Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Distributors.Kyc_Status_Id Left Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Distributors.Bank_Status_Id Where Users.Id = ?";
        } else {
            sql = " Select Users.Firstname, Users.Lastname, Users.Phone, Users.Email, Users.Gender, To_Char(To_Timestamp(Users.Dob/1000) at Time Zone 'Asia/Kolkata', 'YYYY-MM-DD') Dob, Users.Avatar From Users Where Users.Id = ?";
        }
        return Handler.findFirst(sql, userid);
    }

    public MapResponse profile(
            String firstname, 
            String lastname, 
            String dob, String gender, 
            String phone, 
            String email, 
            String address_1, 
            String address_2, 
            String landmark , 
            String postcode, 
            String city, 
            String state, 
            String nominee_name, 
            String nominee_relation) {
        if (!CommonUtil.validateGender(gender)) {
            return MapResponse.failure("Invalid Gender provided");
        }
        if (!CommonUtil.validateNomineerelation(nominee_relation)) {
            return MapResponse.failure("Invalid Nomineerelation provided");
        }
        long userid = UserUtil.getUserid();
        if (UserUtil.isDistributor()) {
            String sql = "Select Kyc_Verification_Statuses.Status From Distributors Left Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Distributors.Kyc_Status_Id Where Distributors.Id = ?";
            boolean kyc = KycVerificationStatus.VERIFIED.equals(Handler.getString(sql, userid));
            if (kyc) {
                return MapResponse.failure("Userdata cannot be changed after KYC verification");
            }
        }
        User user = User.findById(userid);
        long dobL = TimeUtil.parse(dob, "yyyy-MM-dd");
        boolean changed = false;
        long time = TimeUtil.currentTime();
        String oldFirstname = user.getString("firstname");
        if (!firstname.equals(oldFirstname)) {
            user.set("firstname", firstname);
            addHistory(userid, "Firstname", oldFirstname, firstname, time);
            changed = true;
        }

        String oldLastname = user.getString("lastname");
        if (!lastname.equals(oldLastname)) {
            user.set("lastname", lastname);
            addHistory(userid, "Lastname", oldLastname, lastname, time);
            changed = true;
        }

        Long oldDob = user.getLong("dob");
        if (oldDob == null || dobL != oldDob) {
            user.set("dob", dobL);
            addHistory(userid, "Date of Birth", oldDob != null ? oldDob : null, dobL, time);
            changed = true;
        }

        String oldGender = user.getString("gender");
        if (!gender.equals(oldGender)) {
            user.set("gender", gender);
            addHistory(userid, "Gender", oldGender, gender, time);
            changed = true;
        }

        String oldPhone = user.getString("phone");
        if (!phone.equals(oldPhone)) {
            user.set("phone", phone);
            addHistory(userid, "Phone", oldPhone, phone, time);
            changed = true;
        }

        String oldEmail = user.getString("email");
        if (!email.equals(oldEmail)) {
            user.set("email", email);
            addHistory(userid, "Email", oldEmail, email, time);
            changed = true;
        }

        String oldAddress1 = user.getString("address_1");
        if (!address_1.equals(oldAddress1)) {
            user.set("address_1", address_1);
            addHistory(userid, "Address1", oldAddress1, address_1, time);
            changed = true;
        }

        String oldAddress2 = user.getString("address_2");
        if (!address_2.equals(oldAddress2)) {
            user.set("address_2", address_2);
            addHistory(userid, "Address2", oldAddress2, address_2, time);
            changed = true;
        }

        String oldLandmark = user.getString("landmark");
        if (!landmark.equals(oldLandmark)) {
            user.set("landmark", landmark);
            addHistory(userid, "Landmark", oldLandmark, landmark, time);
            changed = true;
        }

        String oldPostcode = user.getString("postcode");
        if (!postcode.equals(oldPostcode)) {
            user.set("postcode", postcode);
            addHistory(userid, "Postcode", oldPostcode, postcode, time);
            changed = true;
        }

        String oldCity = user.getString("city");
        if (!city.equals(oldCity)) {
            user.set("city", city);
            addHistory(userid, "City", oldCity, city, time);
            changed = true;
        }

        String oldState = user.getString("state");
        if (!state.equals(oldState)) {
            user.set("state", state);
            addHistory(userid, "State", oldState, state, time);
            changed = true;
        }

        String oldNomineeName = user.getString("nominee_name");
        if (!nominee_name.equals(oldNomineeName)) {
            user.set("nominee_name", nominee_name);
            addHistory(userid, "Nominee Name", oldNomineeName, nominee_name, time);
            changed = true;
        }


        String oldNomineeRelation = user.getString("nominee_relation");
        if (!nominee_relation.equals(oldNomineeRelation)) {
            user.set("nominee_relation", nominee_relation);
            addHistory(userid, "Nominee Relation", oldNomineeRelation, nominee_relation, time);
            changed = true;
        }

        if (changed) {
            return user.saveIt() ? MapResponse.success() : MapResponse.failure();
        }
        return MapResponse.success();
    }

    private void addHistory(long userid, String field, Object oldValue, Object newValue, long time) {
        UserHistory history = new UserHistory();
        history.set("user_id", userid);
        history.set("field", field);
        history.set("old_value", String.valueOf(oldValue));
        history.set("new_value", String.valueOf(newValue));
        history.set("owner_id", userid);
        history.set("time", time);
        history.insert();
    }

    public MapResponse password(String old, String password) {
        String sql = "Select Password From Users Where Id = ?";
        long userid = UserUtil.getUserid();
        String currentPassword = Handler.getString(sql, userid);
        if (NoPasswordEncoder.getInstance().matches(old, currentPassword)) {
            return User.update("Password = ?", "Id = ?", password, userid) == 1 ? MapResponse.success()
                    : MapResponse.failure();
        } else {
            return MapResponse.failure("Wrong Old Password");
        }
    }

    public MapResponse fetch(String username) {
        String sql = "Select Users.Firstname User_Firstname, Users.Lastname User_Lastname, Addresses.Firstname, Addresses.Lastname, Addresses.Phone, Addresses.Alt_Phone, Addresses.Email, Addresses.Address_1, Addresses.Address_2, Addresses.Postcode, Addresses.Landmark, Addresses.City, Addresses.State, Addresses.Country From Users Left Join Addresses On Addresses.User_Id = Users.Id Where Users.Email = ? Or Users.Phone = ? Order By Addresses.Is_Default Limit 1";
        MapResponse response = Handler.findFirst(sql, username, username);
        return response == null ? MapResponse.failure("No User found") : response;
    }

    public MapResponse avatar(MultipartFile avatar) {
        long id = UserUtil.getUserid();
        String img = ImageUtil.uploadDistributorDp(avatar, id + "-dp");
        if (img == null) {
            return MapResponse.failure("Error with image. Contact Support");
        }
        return User.update("avatar = ?", "id = ?", img, id) == 1 ? MapResponse.success().put("avatar", img) : MapResponse.failure();
    }
}
