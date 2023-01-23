package it.bz.odh.spreadsheets.mapper;

import java.util.List;

import com.google.api.services.sheets.v4.model.Sheet;

import it.bz.odh.spreadsheets.dto.MappingResult;

public interface ISheetMapper {
    public MappingResult mapSheet(List<List<Object>> values, Sheet sheet);
}