package vn.ifine.dto;

import java.time.LocalDate;

public record DailyNewBooksDto(LocalDate day, long newBooks) {}
