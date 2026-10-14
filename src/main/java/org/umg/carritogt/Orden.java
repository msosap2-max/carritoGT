package org.umg.carritogt;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private String estado = "pendiente";

    private BigDecimal total = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<OrdenDetalle> detalles = new ArrayList<>();
}