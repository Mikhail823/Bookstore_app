package com.example.bookshop.service.impl;

import com.example.bookshop.repository.FaqRepository;
import com.example.bookshop.service.FaqService;
import com.example.bookshop.struct.other.FaqEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

    @Autowired
    private final FaqRepository faqRepository;

    @Override
    @Cacheable(value = "faqs")
    public List<FaqEntity> findAllFaq() {
        return faqRepository.findAll();
    }
}
