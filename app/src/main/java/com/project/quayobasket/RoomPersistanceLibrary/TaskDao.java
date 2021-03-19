package com.project.quayobasket.RoomPersistanceLibrary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM QuayoBasketDTO" )
    List<QuayoBasketDTO> getAllProduct();

    @Query("SELECT * FROM QuayoBasketDTO WHERE productid=:productid and id =:id" )
    public List<QuayoBasketDTO> getDetail(String productid, String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QuayoBasketDTO task);

    @Delete
    void delete(QuayoBasketDTO task);

    @Update
    void update(QuayoBasketDTO task);

    @Query("delete from QuayoBasketDTO")
    void deleteAll();



}
