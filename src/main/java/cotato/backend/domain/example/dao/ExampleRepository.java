package cotato.backend.domain.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cotato.backend.domain.example.entity.ExampleEntity;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
}