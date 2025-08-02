package com.hionstudios.zerroo.flow.item;

import static com.hionstudios.MapResponse.failure;
import static com.hionstudios.MapResponse.success;

import org.springframework.web.multipart.MultipartFile;

import com.hionstudios.CachedSelect;
import com.hionstudios.MapResponse;
import com.hionstudios.datagrid.DataGridParams;
import com.hionstudios.db.Handler;
import com.hionstudios.db.SqlCriteria;
import com.hionstudios.db.SqlQuery;
import com.hionstudios.db.SqlUtil;
import com.hionstudios.zerroo.flow.ImageUtil;
import com.hionstudios.zerroo.model.Image;
import com.hionstudios.zerroo.model.ImageList;
import com.hionstudios.time.TimeUtil;

public class ImageListTransaction {
    public MapResponse view(DataGridParams params) {
        String sql = "Select Image_Lists.Id, Image_Lists.Id \"Action\", Image_Lists.Name, Array(Select Image From Images Where List_id = Image_Lists.Id Order By Index Asc) Images, (Select Count(*) From Items Where Image_Id = Image_Lists.Id) Items From Image_Lists";
        String count = "Select Count(*) From Image_Lists";
        String search = params.getSearch();
        SqlCriteria customCriteria = search == null ? null
                : new SqlCriteria("(Image_Lists.Name iLike ?)", search + "%");
        SqlCriteria criteria = SqlUtil.constructCriteria(params, customCriteria, true);
        SqlCriteria filter = SqlUtil.constructCriteria(params);
        String[] columns = {
                "Action",
                "Name",
                "Images",
                "Items"
        };
        return Handler.toDataGrid(
                new SqlQuery(sql, criteria),
                new SqlQuery(count, filter),
                columns);
    }

    public MapResponse view(long id) {
        String sql = "Select Image, Index From Images Where List_Id = ? Order By Index Asc";
        String name = "Select Name From Image_Lists Where Id = ?";
        return Handler.findFirst(name, id).put("images", Handler.findAll(sql, id));
    }

    public MapResponse add(String name) {
        ImageList imageList = new ImageList();
        CachedSelect.dropCache("image");
        return imageList.set("name", name).saveIt() ? success().put("id", imageList.getId()) : failure();
    }

    public MapResponse removeImage(long list, String image) {
        ImageUtil.delete(image);
        return Image.delete("list_id = ? And Image = ?", list, image) == 1 ? success() : failure();
    }

    public MapResponse addImages(long list, MultipartFile[] images) {
        String sql = "Select Name From Image_Lists Where Id = ?";
        String name = Handler.getString(sql, list);
        for (MultipartFile image : images) {
            String filename = name + "-" + TimeUtil.currentTime();
            String img = ImageUtil.uploadProducts(image, filename);
            new Image(list, img).insert();
        }
        return success();
    }

    public MapResponse update(long id, String name) {
        CachedSelect.dropCache("image");
        return ImageList.update("name = ?", "id = ?", name, id) == 1 ? success() : failure();
    }

    public MapResponse reorder(long id, String[] images) {
        for (int i = 0; i < images.length; i++) {
            Image.update("index = ?", "Image = ? And List_Id = ?", i, images[i], id);
        }
        return success();
    }
}
