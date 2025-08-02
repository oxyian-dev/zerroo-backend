package com.hionstudios.zerroo.flow;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.security.Generator;
import com.hionstudios.time.TimeUtil;
import com.hionstudios.zerroo.mail.MailSenderFrom;
import com.hionstudios.zerroo.mail.MailUtil;
import com.hionstudios.zerroo.model.BankVerification;
import com.hionstudios.zerroo.model.BankVerificationStatus;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.KycVerification;
import com.hionstudios.zerroo.model.KycVerificationStatus;
import com.hionstudios.zerroo.model.Otp;
import com.hionstudios.zerroo.model.OtpType;
import com.hionstudios.zerroo.model.PurchaseWalletRequest;
import com.hionstudios.zerroo.model.PurchaseWalletRequestStatus;
import com.hionstudios.zerroo.model.PurchaseWalletTransactionType;

/**
 * Verification of
 * Phone
 * Email
 * KYC
 * Bank
 */
public class VerificationTransaction {
    /**
     * Send OTP Verification Code to the registered mobile number
     * 
     * @return success or failure
     */
    public MapResponse verifyPhone() {
        long userid = UserUtil.getUserid();
        String sql = "Select Phone From Users Where Id = ?";
        long time = TimeUtil.currentTime();
        String phone = Handler.getString(sql, userid);
        Otp otp = Otp.findFirst(
                "Distributor_Id = ? And Expiry > ? And Type_Id = (Select Id From Otp_Types Where Type = ?)",
                userid, time, OtpType.SMS);
        String code;
        if (otp == null) {
            code = Generator.OTP.generate();
            otp = new Otp(userid, code, OtpType.SMS);
            otp.insert();
        } else {
            code = otp.getString("otp");
        }
        /*
         * SmsApp smsApp = SmsApp.getDefaultApp();
         * if (smsApp.sendOtp(phone, code)) {
         * MapResponse response = MapResponse.success();
         * response.put("id", otp.getId());
         * return response;
         * }
         */
        return MapResponse.failure("Check the number and try again");
    }

    /**
     * Verify the OTP Verification Code
     * 
     * @param id   OTP ID
     * @param code OTP Code
     * @return success or failure
     */
    public MapResponse verifyPhone(long id, String code) {
        long userid = UserUtil.getUserid();
        long time = TimeUtil.currentTime();
        String sql = "Select Id From Otps Where Id = ? And Otp = ? And Distributor_Id = ? And Expiry > ? And Type_Id = (Select Id From Otp_Types Where Type = ?)";
        boolean status = Handler.exists(sql, id, code, userid, time, OtpType.SMS);
        if (status) {
            Otp.delete("Id = ?", id);
            Distributor.update("Phone_Verified = ?", "Id = ?", true, userid);
            return MapResponse.success();
        } else {
            return MapResponse.failure("Otp invalid or expired");
        }
    }

    /**
     * Send verification email to the registered Email address
     * 
     * @return success or failure
     */
    public MapResponse verifyEmail() {
        long userid = UserUtil.getUserid();
        String sql = "Select Email From Users Where Id = ?";
        long time = TimeUtil.currentTime();
        String email = Handler.getString(sql, userid);
        Otp otp = Otp.findFirst(
                "Distributor_Id = ? And Expiry > ? And Type_Id = (Select Id From Otp_Types Where Type = ?)",
                userid, time, OtpType.EMAIL);
        String code;
        if (otp == null) {
            code = Generator.OTP.generate();
            otp = new Otp(userid, code, OtpType.EMAIL);
            otp.insert();
        } else {
            code = otp.getString("otp");
        }
        MailUtil.sendMailAsync(MailSenderFrom.noReply(), email, "Email Verification", code, false);
        MapResponse response = MapResponse.success();
        response.put("id", otp.getId());
        return response;
    }

    /**
     * Verify the OTP sent in the email address
     * 
     * @param id   OTP ID
     * @param code OTP Code
     * @return success or failure
     */
    public MapResponse verifyEmail(long id, String code) {
        long userid = UserUtil.getUserid();
        long time = TimeUtil.currentTime();
        String sql = "Select Id From Otps Where Id = ? And Otp = ? And Distributor_Id = ? And Expiry > ? And Type_Id = (Select Id From Otp_Types Where Type = ?)";
        boolean status = Handler.exists(sql, id, code, userid, time, OtpType.EMAIL);
        if (status) {
            Otp.delete("Id = ?", id);
            Distributor.update("Email_Verified = ?", "Id = ?", true, userid);
            return MapResponse.success();
        } else {
            return MapResponse.failure("Otp invalid or expired");
        }
    }

    /**
     * Initiate a KYC Verification request by an Influencer
     * 
     * @param aadhaar       Aadhaar number
     * @param aadhaar_front Aadhaar Front Image
     * @param aadhaar_back  Aadhaar Back Image
     * @param pan           PAN Number
     * @param pan_firstname PAN Firstname
     * @param pan_lastname  PAN Lastname
     * @param pan_image     PAN Image
     * @return success or failure
     */
    public MapResponse kyc(
            String aadhaar,
            MultipartFile aadhaar_front,
            MultipartFile aadhaar_back,
            String pan,
            String pan_firstname,
            String pan_lastname,
            MultipartFile pan_image) {
        long userid = UserUtil.getUserid();
        String uuid = UUID.randomUUID().toString();
        String aadhaarFront = ImageUtil.uploadKyc(aadhaar_front, "aadhaar-front-" + uuid);
        String aadhaarBack = ImageUtil.uploadKyc(aadhaar_back, "aadhaar-back-" + uuid);
        String panImage = ImageUtil.uploadKyc(pan_image, "pan-" + uuid);
        boolean status = new KycVerification(
                userid, aadhaar, aadhaarFront, aadhaarBack, pan, pan_firstname, pan_lastname, panImage)
                .insert();
        if (status) {
            Distributor.update(
                    "kyc_status_id = (Select Id From Kyc_Verification_Statuses Where Status = ?), Kyc_Rejection_Reason = ?",
                    "id = ?", KycVerificationStatus.PENDING, null, userid);
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    /**
     * Approve or reject an KYC request
     * 
     * @param id     KYC Request ID
     * @param status True for Success and False for Failure
     * @param reason Reason for failure. In case of success, no reason will be
     *               given.
     * @return success or failure
     */
    public MapResponse kyc(long id, boolean status, String reason) {
        KycVerification kycVerification = KycVerification.findById(id);
        long userid = kycVerification.getLong("distributor_id");
        if (status) {
            long statusId = KycVerificationStatus.getId(KycVerificationStatus.VERIFIED);
            kycVerification.set("status", statusId);
            Distributor.update("kyc_status_id = ?, Kyc_Verification_Id = ?",
                    "id = ?", statusId, id, userid);
        } else {
            long statusId = KycVerificationStatus.getId(KycVerificationStatus.REJECTED);
            kycVerification.set("status", statusId);
            kycVerification.set("reason", reason);
            Distributor.update("kyc_status_id = ?, Kyc_Verification_Id = ?, Kyc_Rejection_Reason = ?",
                    "id = ?",
                    statusId,
                    id,
                    reason,
                    userid);
        }
        kycVerification.set("action_by", UserUtil.getUserid());
        kycVerification.set("action_time", TimeUtil.currentTime());
        return kycVerification.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse pendingKyc(DataGridParams params) {
        String sql = "Select Kyc_Verifications.Id, Kyc_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Kyc_Verifications.Created_Time \"Created Time\" From Kyc_Verifications Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users On Users.Id = Kyc_Verifications.Distributor_Id";
        String count = "Select Count(*) From Kyc_Verifications Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users On Users.Id = Kyc_Verifications.Distributor_Id";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Kyc_Verification_Statuses.Status = ?", KycVerificationStatus.PENDING)
                : new SqlCriteria("Users.Username iLike ? And Kyc_Verification_Statuses.Status = ?",
                        search, KycVerificationStatus.PENDING);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time");
    }

    public MapResponse verifiedKyc(DataGridParams params) {
        String sql = "Select Kyc_Verifications.Id, Kyc_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Kyc_Verifications.Created_Time \"Created Time\", Kyc_Verifications.Action_Time \"Action Time\", Admin.Firstname \"Approved By\" From Kyc_Verifications Join Users On Users.Id = Kyc_Verifications.Distributor_Id Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users Admin On Admin.Id = Kyc_Verifications.Action_By";
        String count = "Select Count(*) From Kyc_Verifications  Join Users On Users.Id = Kyc_Verifications.Distributor_Id Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users Admin On Admin.Id = Kyc_Verifications.Action_By";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Kyc_Verification_Statuses.Status = ?", KycVerificationStatus.VERIFIED)
                : new SqlCriteria("Users.Username iLike ? And Kyc_Verification_Statuses.Status = ?",
                        search, KycVerificationStatus.VERIFIED);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time",
                "Action Time",
                "Verified By");
    }

    public MapResponse rejectedKyc(DataGridParams params) {
        String sql = "Select Kyc_Verifications.Id, Kyc_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Kyc_Verifications.Created_Time \"Created Time\", Kyc_Verifications.Action_Time \"Action Time\", Admin.Firstname \"Rejected By\", Kyc_Verifications.Reason From Kyc_Verifications Join Users On Users.Id = Kyc_Verifications.Distributor_Id Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users Admin On Admin.Id = Kyc_Verifications.Action_By";

        String count = "Select Count(*) From Kyc_Verifications Join Users On Users.Id = Kyc_Verifications.Distributor_Id Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Join Users Admin On Admin.Id = Kyc_Verifications.Action_By";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Kyc_Verification_Statuses.Status = ?", KycVerificationStatus.REJECTED)
                : new SqlCriteria("Users.Username iLike ? And Kyc_Verification_Statuses.Status = ?",
                        search, KycVerificationStatus.REJECTED);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time",
                "Action Time",
                "Rejected By",
                "Reason");
    }

    public MapResponse kycDetails(long id) {
        String sql = "Select Firstname, lastname, KYC_Verifications.Pan_Firstname, KYC_Verifications.Pan_Lastname, KYC_Verifications.Pan, KYC_Verifications.Pan_Image, KYC_Verifications.Aadhaar, KYC_Verifications.Aadhaar_Front_Image, KYC_Verifications.Aadhaar_Back_Image, KYC_Verifications.Status From KYC_Verifications Join Users On Users.Id = KYC_Verifications.Distributor_Id Where KYC_Verifications.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse bank(String acc, String ifsc, String bank, String branch, MultipartFile image) {
        long userid = UserUtil.getUserid();
        String filename = UUID.randomUUID().toString();
        String proof = ImageUtil.uploadKyc(image, filename);
        BankVerification verification = new BankVerification(userid, acc, ifsc, bank, branch, proof);
        if (verification.insert()) {
            Distributor.update("bank_status_id = (Select Id From Bank_Verification_Statuses Where Status = ?)",
                    "id = ?", BankVerificationStatus.PENDING, userid);
            return MapResponse.success();
        }
        return MapResponse.failure();
    }

    public MapResponse bank(long id, boolean status, String reason) {
        BankVerification verification = BankVerification.findById(id);
        long userid = verification.getLong("distributor_Id");
        if (status) {
            int statusId = BankVerificationStatus.getId(BankVerificationStatus.VERIFIED);
            verification.set("status", statusId);
            Distributor.update("bank_status_id = ?, Bank_Verification_Id = ?",
                    "id = ?", statusId, id, userid);
        } else {
            int statusId = BankVerificationStatus.getId(BankVerificationStatus.REJECTED);
            verification.set("status", statusId);
            verification.set("reason", reason);
            Distributor.update("Bank_Status_Id = ?, Bank_Rejection_Reason = ?, Bank_Verification_Id = ?",
                    "id = ?", statusId, reason, id, userid);
        }
        verification.set("action_by", UserUtil.getUserid());
        verification.set("action_time", TimeUtil.currentTime());
        return verification.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse pendingBank(DataGridParams params) {
        String sql = "Select Bank_Verifications.Id, Bank_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Bank_Verifications.Created_Time \"Created Time\" From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id";
        String count = "Select Count(*) From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();

        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Bank_Verification_Statuses.Status = ?", BankVerificationStatus.PENDING)
                : new SqlCriteria("Users.Username iLike ? And Bank_Verification_Statuses.Status = ?",
                        search, BankVerificationStatus.PENDING);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time");
    }

    public MapResponse verifiedBank(DataGridParams params) {
        String sql = "Select Bank_Verifications.Id, Bank_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Bank_Verifications.Created_Time \"Created Time\", Bank_Verifications.Action_Time \"Verified Time\", Admin.Firstname \"Verified By\" From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id Join Users Admin On Admin.Id = Bank_Verifications.Action_By";
        String count = "Select Count(*) From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id";

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Bank_Verification_Statuses.Status = ?", BankVerificationStatus.VERIFIED)
                : new SqlCriteria("Users.Username iLike ? And Bank_Verification_Statuses.Status = ?",
                        search, BankVerificationStatus.VERIFIED);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time",
                "Verified Time",
                "Verified By");
    }

    public MapResponse rejectedBank(DataGridParams params) {
        String sql = "Select Bank_Verifications.Id, Bank_Verifications.Id \"Action\", Users.Username ZID, Users.Firstname, Users.Phone, Users.Email, Bank_Verifications.Created_Time \"Created Time\", Bank_Verifications.Action_Time \"Rejected Time\", Admin.Firstname \"Rejected By\", Bank_Verifications.Reason From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id Join Users Admin On Admin.Id = Bank_Verifications.Action_By";
        String count = "Select Count(*) From Bank_Verifications Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Join Users On Users.Id = Bank_Verifications.Distributor_Id";

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Bank_Verification_Statuses.Status = ?", BankVerificationStatus.REJECTED)
                : new SqlCriteria("Users.Username iLike ? And Bank_Verification_Statuses.Status = ?",
                        search, BankVerificationStatus.REJECTED);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                "Action",
                "ZID",
                "Firstname",
                "Phone",
                "Email",
                "Created Time",
                "Rejected Time",
                "Rejected By",
                "Reason");
    }

    public MapResponse bankDetails(long id) {
        String sql = "Select Users.Firstname, Users.Lastname, Bank_Verifications.Ifsc, Bank_Verifications.Account_No, Bank_Verifications.Bank, Bank_Verifications.Branch, Bank_Verifications.Image, Bank_Verifications.Status From Bank_Verifications Join Users On Users.Id = Bank_Verifications.Distributor_Id Where Bank_Verifications.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse walletRequest(
            String method,
            double amount,
            long date,
            String depositor,
            String transaction,
            MultipartFile proofImage) {
        long distributorId = UserUtil.getUserid();

        boolean alreadyExists = Handler.exists(
                "Select Purchase_Wallet_Requests.Id From Purchase_Wallet_Requests Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id And Purchase_Wallet_Request_Statuses.Status != ? Where Purchase_Wallet_Requests.Transaction_Id iLIke ?",
                PurchaseWalletRequestStatus.REJECTED, transaction);
        if (alreadyExists) {
            return MapResponse.failure("This Transaction ID is either Pending or Verified");
        }
        String proof = proofImage != null ? ImageUtil.uploadWalletRequest(proofImage, transaction) : null;
        PurchaseWalletRequest purchaseWalletRequest = new PurchaseWalletRequest(
                distributorId,
                amount,
                date,
                method,
                depositor,
                transaction,
                proof);
        return purchaseWalletRequest.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse pendingWallet(DataGridParams params) {
        String sql = "Select Purchase_Wallet_Requests.Id, Purchase_Wallet_Requests.Id \"Action\", Purchase_Wallet_Requests.Time, Users.Username ZID, Users.Firstname, Purchase_Wallet_Requests.Amount, Purchase_Wallet_Requests.Transaction_Id \"Transaction Id\" From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";

        String count = "Select Count(*) From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";

        String[] columns = {
                "Action",
                "Time",
                "ZID",
                "Firstname",
                "Amount",
                "Transaction Id"
        };

        HashMap<String, String> mapping = new HashMap<>(1);
        mapping.put("ZID", "Users.Username");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        params.sortColumn = mapping.getOrDefault(params.sortColumn, params.sortColumn);

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ?", PurchaseWalletRequestStatus.PENDING)
                : new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ? And Users.Username iLike ?",
                        PurchaseWalletRequestStatus.PENDING, search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse rejectedWallet(DataGridParams params) {
        String sql = "Select Purchase_Wallet_Requests.Id, Purchase_Wallet_Requests.Id \"Action\", Purchase_Wallet_Requests.Time, Users.Username ZID, Purchase_Wallet_Requests.Amount, Purchase_Wallet_Requests.Transaction_Id \"Transaction Id\", Purchase_Wallet_Requests.Remark, Action_Taker.Firstname \"Rejected By\", Purchase_Wallet_Requests.Action_Time \"Rejected Time\" From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id Join Users Action_Taker On Action_Taker.Id = Purchase_Wallet_Requests.Action_By";

        String count = "Select Count(*) From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";

        String[] columns = {
                "Action",
                "Time",
                "ZID",
                "Amount",
                "Transaction Id",
                "Remark",
                "Rejected By",
                "Rejected Time"
        };

        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("ZID", "Users.Username");
        mapping.put("Transaction Id", "Purchase_Wallet_Requests.Transaction_Id");
        mapping.put("Rejected By", "Action_Taker.Firstname");
        mapping.put("Rejected Time", "Purchase_Wallet_Requests.Action_Time");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }

        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ?", PurchaseWalletRequestStatus.REJECTED)
                : new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ? And Users.Username iLike ?",
                        PurchaseWalletRequestStatus.REJECTED, search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse approvedWallet(DataGridParams params) {
        String sql = "Select Purchase_Wallet_Requests.Id, Purchase_Wallet_Requests.Id \"Action\", Purchase_Wallet_Requests.Time, Users.Username ZID, Purchase_Wallet_Requests.Amount, Purchase_Wallet_Requests.Transaction_Id \"Transaction Id\", Purchase_Wallet_Requests.Remark, Action_Taker.Firstname \"Approved By\", Purchase_Wallet_Requests.Action_Time \"Approved Time\" From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id Join Users Action_Taker On Action_Taker.Id = Purchase_Wallet_Requests.Action_By";

        String count = "Select Count(*) From Purchase_Wallet_Requests Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";

        String[] columns = {
                "Action",
                "Time",
                "ZID",
                "Amount",
                "Transaction Id",
                "Remark",
                "Approved By",
                "Approved Time"
        };
        HashMap<String, String> mapping = new HashMap<>(4);
        mapping.put("ZID", "Users.Username");
        mapping.put("Transaction Id", "Purchase_Wallet_Requests.Transaction_Id");
        mapping.put("Approved By", "Action_Taker.Firstname");
        mapping.put("Approved Time", "Purchase_Wallet_Requests.Action_Time");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null
                ? new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ?", PurchaseWalletRequestStatus.APPROVED)
                : new SqlCriteria("Purchase_Wallet_Request_Statuses.Status = ? And Users.Username iLike ?",
                        PurchaseWalletRequestStatus.APPROVED, search);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse wallet(long id) {
        String sql = "Select Purchase_Wallet_Requests.*, Purchase_Wallet_Request_Statuses.Status, Users.Username, Action_By.Firstname Action_By From Purchase_Wallet_Requests Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id Join Users On Users.Id = Purchase_Wallet_Requests.Distributor_Id Left Join Users Action_By On Action_By.Id = Purchase_Wallet_Requests.Action_By Where Purchase_Wallet_Requests.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse walletStatus(long id, boolean status, String reason) {
        PurchaseWalletRequest purchaseWalletRequest = PurchaseWalletRequest.findFirst("Id = ? And Status_Id = ?",
                id, PurchaseWalletRequestStatus.getId(PurchaseWalletRequestStatus.PENDING));
        long userid = purchaseWalletRequest.getLong("distributor_id");
        if (status) {
            long statusId = PurchaseWalletRequestStatus.getId(PurchaseWalletRequestStatus.APPROVED);
            purchaseWalletRequest.set("status_id", statusId);
            double amount = purchaseWalletRequest.getDouble("amount");
            PurchaseWalletFlow.add(userid, amount, PurchaseWalletTransactionType.WALLET_REQUEST);
        } else {
            long statusId = PurchaseWalletRequestStatus.getId(PurchaseWalletRequestStatus.REJECTED);
            purchaseWalletRequest.set("status_id", statusId);
            purchaseWalletRequest.set("remark", reason);
        }
        purchaseWalletRequest.set("action_by", UserUtil.getUserid());
        purchaseWalletRequest.set("action_time", TimeUtil.currentTime());
        return purchaseWalletRequest.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse walletRequests(DataGridParams params) {
        String sql = "Select Purchase_Wallet_Requests.Id, Purchase_Wallet_Requests.Time, Purchase_Wallet_Requests.Amount, Purchase_Wallet_Requests.Date, Purchase_Wallet_Requests.Bank, Purchase_Wallet_Requests.Method, Purchase_Wallet_Requests.Depositor, Purchase_Wallet_Requests.Transaction_Id \"Transaction Id\", Purchase_Wallet_Requests.Proof, Purchase_Wallet_Request_Statuses.Status, Purchase_Wallet_Requests.Remark, Purchase_Wallet_Requests.Action_Time \"Action Time\" From Purchase_Wallet_Requests Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";
        String count = "Select Count(*) From Purchase_Wallet_Requests Join Purchase_Wallet_Request_Statuses On Purchase_Wallet_Request_Statuses.Id = Purchase_Wallet_Requests.Status_Id";

        String[] columns = {
                "Time",
                "Amount",
                "Date",
                "Bank",
                "Method",
                "Depositor",
                "Transaction Id",
                "Proof",
                "Status",
                "Remark",
                "Action Time"
        };
        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("Transaction Id", "Purchase_Wallet_Requests.Transaction_Id");
        mapping.put("Action Time", "Purchase_Wallet_Requests.Action_Time");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }

        long userid = UserUtil.getUserid();
        SqlCriteria customCriteria = new SqlCriteria("Purchase_Wallet_Requests.Distributor_Id = ?", userid);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }
}
