package com.Y_LAB.homework.model.request;

import lombok.Data;

/**
 * Request объект содержащий тип сортировки и тип места
 * @author Денис Попов
 * @version 1.0
 */
@Data
public class SortPlaceTypeRequest {

    private String sortType;

    private String placeType;
}
