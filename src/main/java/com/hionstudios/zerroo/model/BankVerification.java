package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

import com.hionstudios.time.TimeUtil;

public class BankVerification extends Model {
    public BankVerification() {
    }

    public BankVerification(long userid, String acc, String ifsc, String bank, String branch, String image) {
        set("distributor_id", userid);
        set("account_no", acc);
        set("ifsc", ifsc);
        set("bank", bank);
        set("branch", branch);
        set("image", image);
        set("created_time", TimeUtil.currentTime());
        set("status", BankVerificationStatus.getId(BankVerificationStatus.PENDING));
    }

    public BankVerification(long userid, String acc, String ifsc, String bank, String branch) {
        set("distributor_id", userid);
        set("account_no", acc);
        set("ifsc", ifsc);
        set("bank", bank);
        set("branch", branch);
        set("created_time", TimeUtil.currentTime());
        set("status", BankVerificationStatus.getId(BankVerificationStatus.VERIFIED));
    }
}
