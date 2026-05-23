package project.piuda.domain.memorybook.presentation;

import project.piuda.domain.memorybook.application.MemoryBookService;
import project.piuda.domain.memorybook.application.dto.MemoryBookRequest;
import project.piuda.domain.memorybook.application.dto.MemoryBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/memory-book")
@RequiredArgsConstructor
public class MemoryBookController {

    private final MemoryBookService memoryBookService;

    // 메모리 북 조회 API
    @GetMapping
    public ResponseEntity<MemoryBookResponse> getMemoryBook(@PathVariable Long patientId) {
        return ResponseEntity.ok(memoryBookService.getMemoryBook(patientId));
    }

    // 메모리 북 수정 API
    @PutMapping
    public ResponseEntity<Void> updateMemoryBook(
            @PathVariable Long patientId,
            @RequestBody MemoryBookRequest request) {

        memoryBookService.updateMemoryBook(patientId, request);
        return ResponseEntity.ok().build();
    }
}