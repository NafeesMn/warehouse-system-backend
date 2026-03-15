package com.artiselite.warehouse.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.dashboard.controller.DashboardController;
import com.artiselite.warehouse.dashboard.dto.DashboardSummaryResponse;
import com.artiselite.warehouse.dashboard.service.DashboardService;
import com.artiselite.warehouse.exception.GlobalExceptionHandler;
import com.artiselite.warehouse.inbound.controller.InboundController;
import com.artiselite.warehouse.inbound.dto.response.InboundListItemResponse;
import com.artiselite.warehouse.inbound.service.InboundService;
import com.artiselite.warehouse.outbound.controller.OutboundTransactionController;
import com.artiselite.warehouse.outbound.dto.OutboundTransactionResponse;
import com.artiselite.warehouse.outbound.service.OutboundTransactionService;
import com.artiselite.warehouse.product.controller.ProductController;
import com.artiselite.warehouse.product.dto.response.ProductListItemResponse;
import com.artiselite.warehouse.product.service.ProductService;
import com.artiselite.warehouse.user.controller.MyProfileController;
import com.artiselite.warehouse.user.controller.UserController;
import com.artiselite.warehouse.user.dto.response.MyProfileResponse;
import com.artiselite.warehouse.user.dto.response.UserListItemResponse;
import com.artiselite.warehouse.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        UserController.class,
        MyProfileController.class,
        InboundController.class,
        OutboundTransactionController.class,
        ProductController.class,
        DashboardController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class,
        GlobalExceptionHandler.class
})
class AccessControlWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private InboundService inboundService;

    @MockBean
    private OutboundTransactionService outboundTransactionService;

    @MockBean
    private ProductService productService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        when(userService.getUsers(any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PagedResponse<>(List.of(), 0, 10, 0, 0, true, true));
        when(userService.getMyProfile(anyString()))
                .thenReturn(new MyProfileResponse(
                        2L,
                        "Warehouse Operator",
                        "operator@artiselite.local",
                        "OPERATOR",
                        true,
                        LocalDateTime.of(2026, 3, 15, 8, 0),
                        LocalDateTime.of(2026, 3, 15, 8, 30)
                ));
        when(inboundService.getInbounds(any(), any(), anyInt(), anyInt(), anyString()))
                .thenReturn(new PagedResponse<InboundListItemResponse>(List.of(), 0, 10, 0, 0, true, true));
        when(outboundTransactionService.getAllOutboundTransactions())
                .thenReturn(List.<OutboundTransactionResponse>of());
        when(productService.getProducts(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PagedResponse<ProductListItemResponse>(List.of(), 0, 10, 0, 0, true, true));
        when(dashboardService.getSummary())
                .thenReturn(new DashboardSummaryResponse(2, 5, 1, 1, 3, 2, 1));
    }

    @Test
    @WithMockUser(username = "manager@artiselite.local", roles = "MANAGER")
    void managerCanAccessManagerOnlyEndpoints() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "operator@artiselite.local", roles = "OPERATOR")
    void operatorIsForbiddenFromManagerOnlyEndpoints() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "operator@artiselite.local", roles = "OPERATOR")
    void operatorCanAccessInboundEndpoints() throws Exception {
        mockMvc.perform(get("/api/inbounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "operator@artiselite.local", roles = "OPERATOR")
    void operatorCanAccessOutboundEndpoints() throws Exception {
        mockMvc.perform(get("/api/outbounds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "operator@artiselite.local", roles = "OPERATOR")
    void ownProfileEndpointWorks() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("operator@artiselite.local"));
    }
}