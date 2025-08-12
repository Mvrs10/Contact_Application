package MKP.contacts.service;

import MKP.contacts.domain.Contact;
import MKP.contacts.repo.ContactRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static MKP.contacts.constant.AppConstant.IMG_DIR;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author MKP
 * @version 1.0
 * @serial pmka42@gmail.com
 * @implNote Service Design
 * @since 08/2025
 */

@Service // Mark class as Spring service bean(component)
@Slf4j // Logger from Lombok
@Transactional(rollbackOn = Exception.class) // Safeguard if method throws exception
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepo contactRepo;

    public Page<Contact> getAllContacts(int page, int size){
        return contactRepo.findAll(PageRequest.of(page,size, Sort.by("name")));
    }

    public Contact getContactById(String id){
        return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact contact) {
        return contactRepo.save(contact);
    }

    public void deleteContact(Contact contact) {
        contactRepo.delete(contact);
    }

    public String uploadImg(String id, MultipartFile file) {
        log.info("Saving profile picture for userId: " + id);
        Contact contact = getContactById(id);
        String photoUrl = saveImgFnc.apply(id, file);
        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> name.substring(filename.lastIndexOf("."))).orElse(".png");
    // Take String, MultipartFile and return a String
    private final BiFunction<String, MultipartFile, String> saveImgFnc = (id, image) -> {
        String fileName = fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(IMG_DIR).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(id + fileExtension.apply(image.getOriginalFilename())), REPLACE_EXISTING); // Take the data by getInputStream and save to the directory
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + fileName).toUriString();
        }
        catch (Exception e){
            throw new RuntimeException("Unable to save image.");
        }
    };
}
