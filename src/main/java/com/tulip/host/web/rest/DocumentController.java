package com.tulip.host.web.rest;

import com.tulip.host.service.DocumentService;
import com.tulip.host.web.rest.vm.DocumentVM;
import com.tulip.host.web.rest.vm.GenericFilterVM;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
//
//    private final StateMachineService<String, String>  stateMachineService;
//
//    private final StateMachineFactory<String, String> stateMachineFactory;

    @PostMapping
    public void add(@RequestBody DocumentVM vm){
        ResponseEntity<Long> longResponseEntity = documentService.uploadDocument(vm);
    }

    @PostMapping("/list")
    public List<DocumentVM> list(@RequestBody GenericFilterVM filterDTO) {
       return documentService.listDocuments(filterDTO);
    }

    @DeleteMapping
    public void delete(@RequestParam Long id){
         documentService.deleteDocument(id);
     }
}
