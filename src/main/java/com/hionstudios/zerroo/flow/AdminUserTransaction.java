package com.hionstudios.zerroo.flow;

import java.util.HashMap;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.time.TimeUtil;
import com.hionstudios.zerroo.flow.cutoff.IncomeTransaction;
import com.hionstudios.zerroo.model.BankVerification;
import com.hionstudios.zerroo.model.BankVerificationStatus;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.IncomeWalletTransactionType;
import com.hionstudios.zerroo.model.KycVerification;
import com.hionstudios.zerroo.model.KycVerificationStatus;
import com.hionstudios.zerroo.model.PurchaseWalletTransactionType;
import com.hionstudios.zerroo.model.User;
import com.hionstudios.zerroo.model.UserRole;
import com.hionstudios.zerroo.model.UserType;

public class AdminUserTransaction {
    public MapResponse addUser(String firstname, String lastname, String email, String password, long[] roles) {
        String sql = "Select Max(Users.id) + 1 Id From Users";
        User user = new User();
        user.set("id", Handler.getLong(sql));
        user.set("firstname", firstname);
        user.set("lastname", lastname);
        user.set("email", email);
        user.set("password", password);
        user.set("type_id", UserType.getId(UserType.ORGANISATION_USER));

        if (user.insert()) {
            return addRole(user.getLongId(), roles);
        }
        return MapResponse.failure();
    }

    public MapResponse editRoles(long user, long[] roles) {
        UserRole.delete("user_id = ?", user);
        return addRole(user, roles);
    }

    private MapResponse addRole(long user, long[] roles) {
        for (long role : roles) {
            if (!new UserRole(user, role).insert()) {
                return MapResponse.failure();
            }
        }
        return MapResponse.success();
    }

    public MapResponse viewOrgUsers(DataGridParams params) {
        SqlCriteria customCriteria = new SqlCriteria("(User_Types.Type = ?)", UserType.ORGANISATION_USER);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        String sql = "Select Users.Id, Users.Id \"Action\", Users.Firstname, Users.Email, Array(Select Roles.Role From Roles Join User_Roles On User_Roles.Role_Id = Roles.Id And User_Roles.User_Id = Users.Id) Roles from Users Join User_Types On User_Types.Id = Users.Type_Id";
        String countSql = "Select Count(*) From Users Join User_Types On User_Types.Id = Users.Type_Id";
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(countSql, filter),
                "Action", "Firstname", "Email", "Roles");
    }

    public MapResponse viewDistributors(DataGridParams params) {
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? new SqlCriteria("(User_Types.Type = ?)",
                UserType.DISTRIBUTOR)
                : new SqlCriteria(
                        "(User_Types.Type = ?) And (Users.Username iLike ? Or Users.Phone Like ? Or Users.Email iLike ?)",
                        UserType.DISTRIBUTOR, search, search, search);
        HashMap<String, String> mapping = new HashMap<>(5);
        mapping.put("ZID", "Username");
        mapping.put("Self PV", "Self_Pv");
        mapping.put("Income Wallet", "Income_Wallet");
        mapping.put("Purchase Wallet", "Purchase_Wallet");
        mapping.put("Created Time", "Users.Created_Time");
        mapping.put("Kyc Status", "Kyc_Verification_Statuses.Status");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);

        String sql = "Select Users.Id, Users.Id \"Action\", Users.Username ZID, Users.Firstname, Distributors.Active, Distributors.Self_Pv \"Self PV\", Distributors.Sp_Pv \"Sp PV\", Ranks.Rank, Distributors.Income_Wallet \"Income Wallet\", Distributors.Purchase_Wallet \"Purchase Wallet\", Distributors.Referer_Id \"Referer Id\",Distributors.Cutoff_Left_PV \"Cutoff Left Pv\", Distributors.Cutoff_Right_Pv \"Cutoff Right Pv\", Distributors.Carry_Left_Pv \"Backup Left Pv\", Distributors.Carry_Right_Pv \"Backup Right Pv\", Distributors.Total_Left_Pv \"Total Left Pv\", Distributors.Total_Right_Pv \"Total Right Pv\", Distributors.Total_Income \"Total Earnings\", Distributors.Pair_Match_Income \"Pairmatch Income\", Distributors.Sp_Income \"Sp Income\", Distributors.Parent_Id \"Immediate Upline Id No\", Kyc_Verifications.Aadhaar \"Aadhar Number\", Kyc_Verifications.Pan \"Pan Number\", Bank_Verifications.Account_No \"Bank Account\" , Bank_Verifications.Ifsc \"IFSc Code\", Users.Phone, Users.Email, Users.Created_Time \"Created Time\", Kyc_Verification_Statuses.Status \"Kyc Status\" From Users Join Distributors On Distributors.Id = Users.Id Join User_Types On User_Types.Id = Users.Type_Id Left Join Ranks On Ranks.Id = Distributors.Rank_Id Left join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Distributors.Kyc_Status_Id Left Join Kyc_Verifications On Distributors.Kyc_Verification_Id = Kyc_Verifications.Id Left Join Bank_Verifications On Distributors.Bank_Verification_Id = Bank_Verifications.Id";

        String countSql = "Select Count(*) From Users Join Distributors On Distributors.Id = Users.Id Join User_Types On User_Types.Id = Users.Type_Id Left Join Ranks On Ranks.Id = Distributors.Rank_Id Left join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Distributors.Kyc_Status_Id";

        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(countSql, filter),
                "Action",
                "ZID",
                "Firstname",
                "Active",
                "Self PV",
                "Sp PV",
                "Rank",
                "Income Wallet",
                "Purchase Wallet",
                "Phone",
                "Email",
                "Created Time",
                "Kyc Status",
                "Referer Id",
                "Cutoff Left Pv",
                "Cutoff Right Pv",
                "Backup Left Pv",
                "Backup Right Pv",
                "Total Left Pv",
                "Total Right Pv",
                "Total Earnings", 
                "Pairmatch Income",
                "Sp Income",
                "Immediate Upline Id No",
                "Aadhar Number",
                "Pan Number",
                "Bank Account",
                "IFSc Code");
    }

    public MapResponse viewDistributor(long id) {
        String sql = "Select Users.Username, Users.Firstname, Users.Lastname, Users.Phone, Users.Email, Users.Avatar, Kyc_Verifications.Aadhaar, Kyc_Verifications.Aadhaar_Front_Image, Kyc_Verifications.Aadhaar_Back_Image, Kyc_Verifications.Pan, Kyc_Verifications.Pan_Firstname, Kyc_Verifications.Pan_Lastname, Kyc_Verifications.Pan_Image, Kyc_Verification_Statuses.Status Kyc_Verification_Status, Bank_Verifications.Bank, Bank_Verifications.Branch, Bank_Verifications.Ifsc, Bank_Verifications.Account_No, Bank_Verifications.Image Bank_Image, Bank_Verification_Statuses.Status Bank_Verification_Status, Distributors.Referer_Id, Distributors.Self_Pv, Distributors.Cutoff_Self_Pv, Distributors.Cutoff_Left_Pv, Distributors.Cutoff_Right_Pv, Distributors.Carry_Left_Pv, Distributors.Carry_Right_Pv, Distributors.Sp_Pv, Referer.Firstname Referer_Firstname, Referer.Lastname Referer_Lastname, Referer.Username Referer_Username, Users.Created_Time, Distributors.Purchase_Wallet, Distributors.Income_Wallet From Users Join Distributors On Distributors.Id = Users.Id Left Join Bank_Verifications On Bank_Verifications.Id = Distributors.Bank_Verification_Id Left Join Bank_Verification_Statuses On Bank_Verification_Statuses.Id = Bank_Verifications.Status Left Join Kyc_Verifications On Kyc_Verifications.Id = Distributors.Kyc_Verification_Id Left Join Kyc_Verification_Statuses On Kyc_Verification_Statuses.Id = Kyc_Verifications.Status Left Join Users Referer On Referer.Id = Distributors.Referer_Id Where Users.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse editDistributor(long id, String firstname, String lastname, String phone, String email,
            String referer) {
        Distributor.update("referer_id = (Select Id From Users Where Username = ?)", "id = ?", referer, id);
        User user = User.findById(id);
        user.set("firstname", firstname);
        user.set("lastname", lastname);
        user.set("phone", phone);
        user.set("email", email);
        return user.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse uplines(long id, DataGridParams params) {
        String sql = String.format(
                "With Recursive UpLine as (Select Distributors.Id, Users.Username, Users.Firstname, Users.Lastname, Ranks.Rank, Case When Distributors.Placement = 1 Then 'Left' When Placement = 2 Then 'Right' Else '' End Placement, Users.Phone, Distributors.Parent_Id From Distributors Join Users On Users.Id = Distributors.Id And Users.Id = %d Left Join Ranks On Ranks.Id = Distributors.Rank_Id Union All Select Distributors.Id, Users.Username, Users.Firstname, Users.Lastname, Ranks.Rank, Case When Distributors.Placement = 1 Then 'Left' Else 'Right' End Placement, Users.Phone, Distributors.Parent_Id From Distributors Join Users On Users.Id = Distributors.Id Left Join Ranks On Ranks.Id = Distributors.Rank_Id Join UpLine On UpLine.Parent_Id = Distributors.Id) Select * From UpLine",
                id);
        return Handler.toDataGrid(sql, "Username", "Firstname", "Lastname", "Rank", "Placement", "Phone");
    }

    public MapResponse status(long id, boolean status) {
        Distributor.update("active = ?", "Id = ?", status, id);
        return MapResponse.success();
    }

    public MapResponse transfer(
            String username,
            String destination,
            boolean placement,
            String referer) {
        String sql;

        long userid = UserUtil.getIdFromUsername(username);
        long destinationId = UserUtil.getIdFromUsername(destination);
        long refererId = UserUtil.getIdFromUsername(referer);

        if (placement) {
            sql = "Select Id From Distributors Where Left_Id Is Null And Distributors.Id = ?";
        } else {
            sql = "Select Id From Distributors Where Right_Id Is Null And Distributors.Id = ?";
        }
        boolean exists = Handler.exists(sql, destinationId);
        if (!exists) {
            return MapResponse.failure("Given Placement is already occupied");
        }
        String oldDetailsSql = "Select Parent_Id, Placement = 1 Placement From Distributors Where Id = ?";
        MapResponse oldDetails = Handler.findFirst(oldDetailsSql, userid);

        boolean oldPlacement = oldDetails.getBoolean("placement");
        long oldPosition = oldDetails.getLong("parent_id");

        if (oldPlacement) {
            Distributor.update("Left_Id = Null", "Id = ?", oldPosition);
        } else {
            Distributor.update("Right_Id = Null", "Id = ?", oldPosition);
        }
        if (placement) {
            Distributor.update("Left_Id = ?", "Id = ?", userid, destinationId);
        } else {
            Distributor.update("Right_Id = ?", "Id = ?", userid, destinationId);
        }
        Distributor.update(
                "Parent_Id = ?, Referer_Id = ?, Placement = ?",
                "Id = ?", destinationId, refererId, placement ? 1 : 2, userid);

        return MapResponse.success();
    }

    public MapResponse addPv(long id, double pv, boolean recursive) {
        if (recursive) {
            GenealogyUtil.addPv(id, pv);
        } else {
            Distributor.update("Self_Pv = Self_Pv + ?, Cutoff_Self_Pv = Cutoff_Self_Pv + ?",
                    "Id = ?", pv, pv, id);
        }
        return MapResponse.success();
    }

    public MapResponse wallet(long id, double amount, String type) {
        if ("purchase".equals(type)) {
            PurchaseWalletFlow.add(id, amount, PurchaseWalletTransactionType.COMPANY);
        } else if ("income".equals(type)) {
            Distributor distributor = Distributor.findById(id);
            IncomeTransaction.addIncome(distributor, amount, IncomeWalletTransactionType.COMPANY);
            distributor.saveIt();
        }
        return MapResponse.success();
    }

    public MapResponse bank(long id, String ifsc, String bank, String branch, String account_no) {
        BankVerification.update("Status = ?, Action_By = ?, Action_Time = ?, Reason = ?",
                "Distributor_id = ? And Status = ?",
                BankVerificationStatus.getId(BankVerificationStatus.REJECTED),
                UserUtil.getUserid(),
                TimeUtil.currentTime(),
                "Manual update",
                id,
                BankVerificationStatus.getId(BankVerificationStatus.PENDING));
        BankVerification verification = new BankVerification(id, account_no, ifsc, bank, branch);
        verification.insert();
        Distributor.update("bank_status_id = ?, Bank_Verification_Id = ?",
                "id = ?",
                BankVerificationStatus.getId(BankVerificationStatus.PENDING),
                verification.getId(), id);
        return MapResponse.success();
    }

    public MapResponse kyc(long id, String aadhaar, String pan, String pan_firstname, String pan_lastname) {
        KycVerification.update("Status = ?, Action_By = ?, Action_Time = ?, Reason = ?",
                "Distributor_id = ? And Status = ?",
                KycVerificationStatus.getId(KycVerificationStatus.REJECTED),
                UserUtil.getUserid(),
                TimeUtil.currentTime(),
                "Manual update",
                id,
                KycVerificationStatus.getId(KycVerificationStatus.PENDING));
        KycVerification verification = new KycVerification(id, aadhaar, pan, pan_firstname, pan_lastname);
        verification.insert();
        Distributor.update("kyc_status_id = ?, Kyc_Verification_Id = ?",
                "id = ?",
                KycVerificationStatus.getId(KycVerificationStatus.PENDING),
                verification.getId(),
                id);
        return MapResponse.success();
    }

    public MapResponse viewOrgUser(long id) {
        String sql = "Select Firstname, Lastname, Email, Array(Select User_Roles.Role_Id From User_Roles Where User_Roles.User_Id = Users.Id) Roles From Users Where Users.Id = ?";
        return Handler.findFirst(sql, id);
    }

    public MapResponse editUser(long id, String firstname, String lastname, String email, long[] roles) {
        if (id == UserUtil.getUserid()) {
            return MapResponse.failure("User cannot edit his account");
        }
        User.update("Firstname = ?, Lastname = ?, Email = ?", "Id = ?",
                firstname, lastname, email, id);
        return editRoles(id, roles);
    }

    public MapResponse removeAvatar(long id) {
        String sql = "Select Avatar From Users Where Id = ?";
        String avatar = Handler.getString(sql, id);
        ImageUtil.delete(avatar);
        return User.update("avatar = ?", "Id = ?", null, id) == 1 ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse removeKyc(long id) {
        Distributor.update("Kyc_Status_Id = ?, Kyc_Verification_Id = ?", "Id = ?", null, null, id);
        return MapResponse.success();
    }

    public MapResponse removeBank(long id) {
        Distributor.update("Bank_Status_Id = ?, Bank_Verification_Id = ?", "Id = ?", null, null, id);
        return MapResponse.success();
    }
}
