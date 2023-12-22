package com.example.bookshop.service.impl;

import com.example.bookshop.repository.FaqRepository;
import com.example.bookshop.service.FaqService;
import com.example.bookshop.struct.other.FaqEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqServiceImpl implements FaqService {

    private final FaqRepository faqRepository;

    @Autowired
    public FaqServiceImpl(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public List<FaqEntity> findAllFaq() {
        return faqRepository.findAll();
    }
}
