package ch.bfh.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.bfh.domain.Module;
import ch.bfh.repository.ModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Module.
 */
@RestController
@RequestMapping("/app")
public class ModuleResource {

    private final Logger log = LoggerFactory.getLogger(ModuleResource.class);

    @Inject
    private ModuleRepository moduleRepository;

    /**
     * POST  /rest/modules -> Create a new module.
     */
    @RequestMapping(value = "/rest/modules",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Module module) {
        log.debug("REST request to save Module : {}", module);
        moduleRepository.save(module);
    }

    /**
     * GET  /rest/modules -> get all the modules.
     */
    @RequestMapping(value = "/rest/modules",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Module> getAll() {
        log.debug("REST request to get all Modules");
        return moduleRepository.findAll();
    }

    /**
     * GET  /rest/modules/:id -> get the "id" module.
     */
    @RequestMapping(value = "/rest/modules/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Module> get(@PathVariable Long id) {
        log.debug("REST request to get Module : {}", id);
        return Optional.ofNullable(moduleRepository.findOne(id))
            .map(module -> new ResponseEntity<>(
                module,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/modules/:id -> delete the "id" module.
     */
    @RequestMapping(value = "/rest/modules/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Module : {}", id);
        moduleRepository.delete(id);
    }
}
