package org.boot.security.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luoliang
 * @date 2018/7/1
 */
@RestController
@RequestMapping("/main")
@PreAuthorize("hasRole('ADMIN')")
public class MainController {

}
