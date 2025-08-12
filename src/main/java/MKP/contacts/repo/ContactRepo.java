package MKP.contacts.repo;

import MKP.contacts.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author MKP
 * @version 1.0
 * @serial pmka42@gmail.com
 * @implNote Repository to manage the domain/'classes'
 * @since 08/2025
 */

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {
    //Optional<Contact> findbyId(String id);
}
