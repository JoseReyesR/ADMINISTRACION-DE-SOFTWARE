package reclamostottus.reclamos_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reclamostottus.reclamos_backend.dto.HistorialRequestDTO;
import reclamostottus.reclamos_backend.model.EstadoReclamo;
import reclamostottus.reclamos_backend.model.HistorialSeguimiento;
import reclamostottus.reclamos_backend.model.Reclamo;
import reclamostottus.reclamos_backend.repository.HistorialSeguimientoRepository;
import reclamostottus.reclamos_backend.repository.ReclamoRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialSeguimientoServiceTest {

    @Mock
    private HistorialSeguimientoRepository historialRepo;

    @Mock
    private ReclamoRepository reclamoRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private HistorialSeguimientoService historialSeguimientoService;

    private Reclamo reclamo;
    private EstadoReclamo estadoActual;

    @BeforeEach
    void setUp() {
        estadoActual = new EstadoReclamo();
        estadoActual.setId(1);
        estadoActual.setNombre("Registrado");

        reclamo = new Reclamo();
        reclamo.setId(10);
        reclamo.setCodigoSeguimiento("REQ-2026-0001");
        reclamo.setEstado(estadoActual);
    }

    @Test
    void registrarHistorialLanzaExcepcionCuandoElReclamoNoExiste() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(999);

        when(reclamoRepo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> historialSeguimientoService.registrarHistorial(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reclamo no encontrado");

        verifyNoInteractions(historialRepo, emailService);
    }

    @Test
    void registrarHistorialNotaInternaSinCambioDeEstadoNoEnviaCorreo() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(10);
        dto.setUsuarioResponsableId(5);
        dto.setComentario("Nota interna de revisión");
        dto.setEsInterno(true);

        when(reclamoRepo.findById(10)).thenReturn(Optional.of(reclamo));
        when(historialRepo.save(any(HistorialSeguimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialSeguimiento resultado = historialSeguimientoService.registrarHistorial(dto);

        assertThat(resultado.getComentario()).isEqualTo("Nota interna de revisión");
        assertThat(resultado.getEstadoAnterior()).isNull();
        assertThat(resultado.getEstadoNuevo()).isNull();
        verify(reclamoRepo, never()).save(any());
        verifyNoInteractions(emailService);
    }

    @Test
    void registrarHistorialNotaPublicaSinCambioDeEstadoEnviaCorreoSinMencionarEstado() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(10);
        dto.setUsuarioResponsableId(5);
        dto.setComentario("Se revisó su caso");
        dto.setEsInterno(false);

        when(reclamoRepo.findById(10)).thenReturn(Optional.of(reclamo));
        when(historialRepo.save(any(HistorialSeguimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        historialSeguimientoService.registrarHistorial(dto);

        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).enviarCorreoActualizacion(eq("REQ-2026-0001"), detalleCaptor.capture());
        assertThat(detalleCaptor.getValue()).contains("Se revisó su caso");
        assertThat(detalleCaptor.getValue()).doesNotContain("estado de tu reclamo ha sido actualizado");
    }

    @Test
    void registrarHistorialConCambioDeEstadoActualizaElReclamoYEnviaCorreoConMencionDeEstado() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(10);
        dto.setUsuarioResponsableId(5);
        dto.setComentario("Actualizamos tu caso");
        dto.setEsInterno(false);
        dto.setEstadoNuevoId(2);

        when(reclamoRepo.findById(10)).thenReturn(Optional.of(reclamo));
        when(historialRepo.save(any(HistorialSeguimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialSeguimiento resultado = historialSeguimientoService.registrarHistorial(dto);

        assertThat(resultado.getEstadoAnterior()).isSameAs(estadoActual);
        assertThat(resultado.getEstadoNuevo().getId()).isEqualTo(2);
        assertThat(reclamo.getEstado().getId()).isEqualTo(2);
        verify(reclamoRepo, times(1)).save(reclamo);

        ArgumentCaptor<String> detalleCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).enviarCorreoActualizacion(eq("REQ-2026-0001"), detalleCaptor.capture());
        assertThat(detalleCaptor.getValue()).contains("estado de tu reclamo ha sido actualizado");
    }

    @Test
    void registrarHistorialConMismoEstadoNoLoConsideraCambioDeEstado() {
        HistorialRequestDTO dto = new HistorialRequestDTO();
        dto.setReclamoId(10);
        dto.setUsuarioResponsableId(5);
        dto.setComentario("Sin cambios reales");
        dto.setEsInterno(true);
        dto.setEstadoNuevoId(1); // mismo id que el estado actual del reclamo

        when(reclamoRepo.findById(10)).thenReturn(Optional.of(reclamo));
        when(historialRepo.save(any(HistorialSeguimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        HistorialSeguimiento resultado = historialSeguimientoService.registrarHistorial(dto);

        assertThat(resultado.getEstadoAnterior()).isNull();
        assertThat(resultado.getEstadoNuevo()).isNull();
        verify(reclamoRepo, never()).save(any());
    }

    @Test
    void obtenerHistorialCompletoDelegaEnElRepositorio() {
        List<HistorialSeguimiento> lista = List.of(new HistorialSeguimiento());
        when(historialRepo.findByReclamoIdOrderByFechaRegistroAsc(10)).thenReturn(lista);

        List<HistorialSeguimiento> resultado = historialSeguimientoService.obtenerHistorialCompleto(10);

        assertThat(resultado).isSameAs(lista);
    }

    @Test
    void obtenerHistorialPublicoDelegaEnElRepositorio() {
        List<HistorialSeguimiento> lista = List.of(new HistorialSeguimiento());
        when(historialRepo.findByReclamoIdAndEsInternoFalseOrderByFechaRegistroAsc(10)).thenReturn(lista);

        List<HistorialSeguimiento> resultado = historialSeguimientoService.obtenerHistorialPublico(10);

        assertThat(resultado).isSameAs(lista);
    }
}
