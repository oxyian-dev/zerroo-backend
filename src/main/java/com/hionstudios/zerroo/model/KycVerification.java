package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class KycVerification extends Model {
    public KycVerification() {
    }

    public KycVerification(
            long userid,
            String aadhaar,
            String aadhaar_front,
            String aadhaar_back,
            String pan,
            String pan_firstname,
            String pan_lastname,
            String pan_image) {
        set("distributor_id", userid);
        set("aadhaar", aadhaar);
        set("aadhaar_front_image", aadhaar_front);
        set("aadhaar_back_image", aadhaar_back);
        set("pan", pan);
        set("pan_firstname", pan_firstname);
        set("pan_lastname", pan_lastname);
        set("pan_image", pan_image);
        set("created_time", TimeUtil.currentTime());
        set("status", KycVerificationStatus.getId(KycVerificationStatus.PENDING));
    }

    public KycVerification(long userid, String aadhaar, String pan, String pan_firstname, String pan_lastname) {
        set("distributor_id", userid);
        set("aadhaar", aadhaar);
        set("pan", pan);
        set("pan_firstname", pan_firstname);
        set("pan_lastname", pan_lastname);
        set("created_time", TimeUtil.currentTime());
        set("status", KycVerificationStatus.getId(KycVerificationStatus.VERIFIED));
    }
}
