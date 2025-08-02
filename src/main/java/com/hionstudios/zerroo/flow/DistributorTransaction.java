package com.hionstudios.zerroo.flow;

import java.util.HashMap;

import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.security.Generator;
import com.hionstudios.zerroo.mail.MailSenderFrom;
import com.hionstudios.zerroo.mail.MailUtil;
import com.hionstudios.zerroo.model.Distributor;
import com.hionstudios.zerroo.model.User;

public class DistributorTransaction {
    public MapResponse myReferrals(DataGridParams params) {
        long userid = UserUtil.getUserid();
        String sql = "Select Users.Id, Users.Username ZID, Users.Firstname, Users.Lastname, Users.Phone, Distributors.Self_PV \"PV\" From Users Join Distributors On Distributors.Id = Users.Id";
        String count = "Select Count(*) From Users Join Distributors On Distributors.Id = Users.Id";
        String[] columns = {
                "ZID",
                "Firstname",
                "Lastname",
                "Phone",
                "PV"
        };

        HashMap<String, String> mapping = new HashMap<>(2);
        mapping.put("ZID", "Distributors.Username");
        mapping.put("PV", "Distributors.Self_Pv");
        for (int i = 0; i < params.filterColumn.length; i++) {
            params.filterColumn[i] = mapping.getOrDefault(params.filterColumn[i], params.filterColumn[i]);
        }
        SqlCriteria customCriteria = new SqlCriteria("Distributors.Referer_Id = ?", userid);
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params, customCriteria);
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse genealogy(String username) {
        String currentUsername = UserUtil.getUsername();

        long userid = UserUtil.getUserid();
        if (username == null) {
            username = currentUsername;
        } else if (new GenealogyUtil().isDownLine(currentUsername, username)) {
            return MapResponse.failure("This id is not in your Genealogy");
        }

        if (username != null) {
            userid = UserUtil.getIdFromUsername(username);
        }

        String sql = "With Recursive Genealogy As (Select Users.Id, Users.Username, Users.Firstname, Users.Lastname, Users.Avatar, Users.Phone, Distributors.Left_Id, Distributors.Right_Id, Distributors.Total_Left_Pv, Distributors.Carry_Left_Pv, Distributors.Cutoff_Left_Pv, Distributors.Total_Right_Pv, Distributors.Carry_Right_Pv, Distributors.Cutoff_Right_Pv, Distributors.Total_Left_Pv, Distributors.Carry_Left_Pv, Distributors.Cutoff_Left_Pv, Distributors.Total_Right_Pv, Distributors.Carry_Right_Pv, Distributors.Cutoff_Right_Pv, 0 as Level, Distributors.Self_Pv, Distributors.Self_Pv, Distributors.Rank_Id From Distributors Join Users On Users.Id = Distributors.Id Where Users.Username = ? Union All Select Downline.Id, Users.Username, Users.FirstName, Users.Lastname, Users.Avatar, Users.Phone, Downline.Left_Id, Downline.Right_Id, Downline.Total_Left_Pv, Downline.Carry_Left_Pv, Downline.Cutoff_Left_Pv, Downline.Total_Right_Pv, Downline.Carry_Right_Pv, Downline.Cutoff_Right_Pv, Downline.Total_Left_Pv, Downline.Carry_Left_Pv, Downline.Cutoff_Left_Pv, Downline.Total_Right_Pv, Downline.Carry_Right_Pv, Downline.Cutoff_Right_Pv, Genealogy.Level + 1 As Level, Downline.Self_Pv, Downline.Self_Pv, Downline.Rank_Id From Distributors Downline Join Users On Users.Id = Downline.Id Join Genealogy On Genealogy.Id = Downline.Parent_Id Where Genealogy.Level < 3) Select Genealogy.*, Ranks.Rank From Genealogy Left Join Ranks On Ranks.Id = Genealogy.Rank_Id";

        MapResponse resposne = MapResponse.success();
        resposne.put("genealogy", Handler.toJson(sql, "id", username).put("id", userid));
        return resposne;
    }

    public MapResponse refer(
            long parent,
            int placement,
            String firstname,
            String lastname,
            String phone,
            String email,
            String referer) {
        String unique = "Select ID From Users Where Email iLike ?";
        if (Handler.exists(unique, email)) {
            return MapResponse.failure("Email already exists");
        }
        unique = "Select ID From Users Where Phone iLike ?";
        if (Handler.exists(unique, phone)) {
            return MapResponse.failure("Phone already exists");
        }
        long refererId = UserUtil.getIdFromUsername(referer);
        if (refererId != UserUtil.getUserid() && !new GenealogyUtil().isUpLine(parent, refererId)) {
            return MapResponse.failure("Referer should be an upline");
        }

        String password = Generator.PASSWORD.generate();
        User user = new User(firstname, lastname, phone, email, password);

        user.insert();
        String username = user.getString("username");
        long id = user.getLongId();
        Distributor distributor = new Distributor(id, parent, placement, refererId);
        distributor.insert();

        if (placement == 1) {
            Distributor.update("Left_Id = ?", "Id = ?", id, parent);
        } else {
            Distributor.update("Right_Id = ?", "Id = ?", id, parent);
        }
        MailUtil.sendMailAsync(MailSenderFrom.noReply(), email, "Welcome to Zerroo",
                "Username: " + username + "\n" + "Password: " + password, false);
        MapResponse response = MapResponse.success();
        response.put("id", id);
        response.put("username", username);
        response.put("password", password);
        return response;
    }

    public MapResponse dashboard() {
        String sql = "Select Ranks.Rank, Distributors.Self_Pv, Distributors.Sp_Pv, Distributors.Total_Income, Distributors.Pair_Match_Income, Distributors.Sp_Income, Distributors.Income_Wallet, Distributors.Purchase_Wallet, Distributors.Cutoff_Left_Pv, Distributors.Cutoff_Right_Pv, Distributors.Carry_Left_Pv, Distributors.Carry_Right_Pv, Distributors.Total_Left_Pv, Distributors.Total_Right_Pv, (Select Count(*) From Distributors Where Distributors.Referer_Id = ?) Direct_Members, (Select Sum(Sale_Order_Items.Price) + Sale_Orders.Shipping_Fee Price From Sale_Order_Items Join Sale_Orders On Sale_Orders.Id = Sale_Order_Items.Order_Id And Sale_Orders.User_Id = Distributors.Id Group By Sale_Orders.Shipping_Fee) Total_Purchase, Users.Created_Time From Distributors Join Users On Users.Id = Distributors.Id Left Join Ranks On Ranks.Id = Distributors.Rank_Id Where Distributors.Id = ?";

        long userid = UserUtil.getUserid();
        return Handler.findFirst(sql, userid, userid);
    }

    public MapResponse getName(long upline, String username) {
        String sql = "Select Firstname, Lastname, Id From Users Where Username = ?";
        MapResponse user = Handler.findFirst(sql, username);
        long refererId = user.getLong("id");
        if (refererId != UserUtil.getUserid() && !new GenealogyUtil().isUpLine(upline, refererId)) {
            return MapResponse.failure("Referer should be an upline");
        }
        return user;
    }

    public MapResponse getDeclarationStatus() {
        long userid = UserUtil.getUserid();
        String sql = "Select Declaration_Status From Distributors Where Id = ?";
        return Handler.findFirst(sql, userid);
    }

    public MapResponse declaration(boolean declaration_status) {
        long userid = UserUtil.getUserid();

        Distributor distributor = Distributor.findById(userid);
        distributor.set("declaration_status", declaration_status);
        

        return distributor.saveIt()
                ? MapResponse.success("Declaration submitted")
                : MapResponse.failure("Failed to submit declaration status");
    }

}
