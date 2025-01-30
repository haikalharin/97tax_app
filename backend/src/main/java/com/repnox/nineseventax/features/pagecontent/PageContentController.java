package com.repnox.nineseventax.features.pagecontent;

import com.repnox.nineseventax.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController()
@RequestMapping("/api/pageContent")
public class PageContentController {

    private static final Logger LOG = LoggerFactory.getLogger(PageContentController.class);

    @Autowired
    private PageContentRepo pageContentRepo;

    @GetMapping
    public @ResponseBody Iterable<PageContent> getAllPageContents() {
        return pageContentRepo.findAll();
    }

    @PostMapping
    public void postPageContent(@RequestBody PageContent pageContent) {
        pageContentRepo.save(pageContent);
    }

    @PutMapping("/{id}")
    public void putPageContent(@RequestBody PageContent pageContent, @PathVariable Long id) {
        Optional<PageContent> optional = pageContentRepo.findById(id);
        if (optional.isPresent()) {
            pageContentRepo.save(pageContent);
        } else {
            throw new NotFoundException();
        }
    }

    @GetMapping("/{id}")
    public @ResponseBody PageContent getPageContent(@PathVariable Long id) {
        Optional<PageContent> optional = pageContentRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException();
        }
    }

}
