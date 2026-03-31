package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InfractorServiceImpl - Unit Test")
public class InfractorServiceTest {
    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    @DisplayName(" Regresa la Deuda Total con el estado")
    void givenInfractorWithMultas_WhenCalcuDeuda_ThenReturns405(){

        Multa pendiente = new Multa();
        pendiente.setMonto(200.00);
        pendiente.setEstado(EstadoMulta.PENDIENTE);

        Multa vencida = new Multa();
        vencida.setMonto(300.00);
        pendiente.setEstado(EstadoMulta.VENCIDA);

        when(multaRepository.findByInfractor_IdAndEstado(1L, EstadoMulta.PENDIENTE))
                .thenReturn(List.of(pendiente));
        when(multaRepository.findByInfractor_IdAndEstado(1L, EstadoMulta.VENCIDA))
                .thenReturn(List.of(vencida));

        Double resultado = infractorService.calcuDeuda(1L);

        assertEquals(545.00, resultado);
        verify(multaRepository, times(1)).findByInfractor_IdAndEstado(1L, EstadoMulta.PENDIENTE);
        verify(multaRepository, times(1)).findByInfractor_IdAndEstado(1L, EstadoMulta.VENCIDA);

    }

    @Test
    @DisplayName("Desasignar un vehiculo")
    void givenVehiculosSinMultas_whenDesasignarVehiculo_thenRemove(){
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);

        Infractor infractor = new Infractor();
        infractor.setId(1L);
        infractor.setVehiculos(new ArrayList<>(List.of(vehiculo)));

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(multaRepository.existsByVehiculo_IdAndInfractor_IdAndEstado(1L, 1L, EstadoMulta.PENDIENTE)).thenReturn(false);

        infractorService.desasignarVehiculo(1L, 1L);

        assertFalse(infractor.getVehiculos().stream().anyMatch(v -> v.getId().equals(1L)));
        verify(infractorRepository, times(1)).save(infractor);
    }
}
