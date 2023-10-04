package com.example.bookshop.service;

import com.example.bookshop.struct.other.FaqEntity;

import java.util.List;

public interface FaqService {

    List<FaqEntity> findAllFaq();
}
