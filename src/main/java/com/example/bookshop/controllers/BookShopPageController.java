package com.example.bookshop.controllers;

import com.example.bookshop.dto.MessageFormDto;
import com.example.bookshop.service.DocumentService;
import com.example.bookshop.service.FaqService;
import com.example.bookshop.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BookShopPageController {

    private static final String REDIRECT_CONT = "redirect:/contacts";

    @Autowired
    private final DocumentService documentService;

    @Autowired
    private final MessageService messageService;

    @Autowired
    private final FaqService faqService;

    @GetMapping("/about")
    public String handlerAbout(){
        return "about";
    }

    @GetMapping("/documents")
    public String handlerDocuments(Model model){

        model.addAttribute("documents", documentService.getAllDocument());

        return "documents/index";
    }

    @GetMapping("/documents/{slug}")
    public String handlerDocumentBySlug(@PathVariable(value = "slug" , required = false) String slug, Model model){

        model.addAttribute("document", documentService.findDocumentBySlug(slug));

        return "documents/slug";
    }

    @GetMapping("/contacts")
    public String handlerContacts(){
        return "contacts";
    }

    @GetMapping("/faq")
    public String handlerFaq(Model model){
        model.addAttribute("faqList", faqService.findAllFaq());
        return "faq";
    }

    @PostMapping("/message")
    public String handlerMessage(@Validated MessageFormDto formDto){
        messageService.saveMessage(formDto);
        return REDIRECT_CONT;
    }


}
