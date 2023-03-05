package com.jp.pssior.assignment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookShow implements Serializable {

    private String showNo;
    private String phoneNo;
    private Set<String> seats;

}
