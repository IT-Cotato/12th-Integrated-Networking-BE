package cotato.backend.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderTypeAndProviderId(ProviderType providerType, String providerId);

}
