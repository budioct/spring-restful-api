package com.tutorial.restful.api.service;

import com.tutorial.restful.api.dto.ContactResponse;
import com.tutorial.restful.api.dto.CreateContactRequest;
import com.tutorial.restful.api.dto.SearchContactRequest;
import com.tutorial.restful.api.dto.UpdateContactRequest;
import com.tutorial.restful.api.entity.Contact;
import com.tutorial.restful.api.entity.User;
import com.tutorial.restful.api.repository.ContactRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {

        validationService.validate(request); // tangkap constraint validation

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact); // save DB

        return toContactResponse(contact);
    }

    // method response Contact Response
    private ContactResponse toContactResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build(); // response
    }

    @Transactional(readOnly = true) // readOnly() = true Bendera boolean yang dapat disetel ke true jika transaksi hanya dapat dibaca secara efektif, memungkinkan pengoptimalan yang sesuai saat runtime.
    public ContactResponse get(User user, String id) {

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);

    }

    @Transactional
    public ContactResponse update(User user, UpdateContactRequest request) {

        validationService.validate(request); // tangkap constraint validation

        // cek apakah ada username dan id, jika ada kasih. jika tidak ada Exception dengan status
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact); // save DB

        return toContactResponse(contact);
    }

    @Transactional
    public void delete(User user, String contactId) {

        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));


        contactRepository.delete(contact); // delete DB

    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {

        // Specification<T> fitur Criteria query secara dinamis
        // Specification adalah lambda yang return Predicate biasanya return value boolean
        // Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) // anonymouse class dari Implementasi Specifitation<T>
        Specification<Contact> specification = new Specification<Contact>() {
            @Override
            public Predicate toPredicate(Root<Contact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                // karena pencarian DTO/SearchContactRequest yang lumanayan banyak. kita gunakan List<T>
                List<Predicate> predicates = new ArrayList<>();

                // CriteriaBuilder digunakan untuk membuat CriteriaQuery
                // CriteriaQuery(untuk add informasi query yang di lakukan, select dari entity, field apa yang akan di ambil, kondisi where apa yang akan digunakan)
                // Root<Contact> // akan select Entity yang sudah di set

                // boolean add(E e) tambahkan predicate
                // Predicate equal(Expression<?> var1, Object var2) // Buat predicate untuk menguji argumenParameter kesetaraan.
                // <Y> Path<Y> get(String var1) // Buat jalur yang sesuai dengan atribut yang direferensikan, dari Method Resolver Argument User
                boolean addAttribute = predicates.add(criteriaBuilder.equal(root.get("user"), user));
                log.info("Add Attribute is = {}", addAttribute);

                // jika SearchContactRequest null lakukan Criteria Builder dengan jalur yang sesuai dengan attribut DTO/SearchContactRequest
                if (Objects.nonNull(request.getName())){
                    // boolean add(E e) //  tambahkan predicate
                    // Predicate or(Expression<Boolean> var1, Expression<Boolean> var2) // Buat disjungsi dari ekspresi boolean yang diberikan.
                    predicates.add(criteriaBuilder.or(
                    // Predicate like(Expression<String> var1, String var2) // Buat predicate untuk menguji apakah ekspresi memenuhi pola yang diberikan.
                    // <Y> Path<Y> get(String var1) // Buat jalur yang sesuai dengan atribut yang direferensikan, dari referece argument DTO/SearchContactRequest.
                    // query: select c.* from contacts c left join users u on (u.username = c.username) where first_name like= %?% or last_name like = %?%;
                            criteriaBuilder.like(root.get("firstName"), "%" + request.getName() +"%"),
                            criteriaBuilder.like(root.get("lastName"), "%" + request.getName() +"%")
                    ));
                }
                if (Objects.nonNull(request.getEmail())){
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%")
                    ));
                }
                if (Objects.nonNull(request.getPhone())){
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(root.get("phone"), "%" + request.getPhone() + "%")
                    ));
                }

                // CriteriaQuery<T> where(Predicate... var1) // Ubah kueri untuk membatasi hasil kueri sesuai dengan konjungsi predicate pembatasan yang ditentukan.
                // <T> T[] toArray(T[] a) // return Predicate sebagai array yang berisi semua elemen dalam daftar ini dalam urutan yang tepat (dari elemen pertama hingga terakhir); jenis runtime dari larik yang dikembalikan adalah dari larik yang ditentukan.
                // Predicate getRestriction() // return predicate yang sesuai dengan batasan clause mana, atau null jika tidak ada batasan yang ditentukan.
                return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
            }
        };

        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize()); // static PageRequest of(int page, int size) // implementasi PageRequest dari interface Page<T> //
        Page<Contact> contacts = contactRepository.findAll(specification, pageable); // Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) // temukan semua spesifikasi yang sudah di tentukan dan pagebale yang sudah di tentukan // akan return data pencarian dari DB dan Page Result (Page<T> otomatis akan ambil informasi total data dan total halaman)

        // karena kita method return Page<ContactResponse>, tetapi hasilnya return Page<Contact> dari query. maka kita akan konversi supaya bisa return ContactResponse kita rubah menjadi List<T>
        // List<T> getContent() // return Page<T> kontent halaman sebagai List<T>
        // Stream<E> stream() // konversi List<T> ke Stream<T>
        // <R> Stream<R> map(Function<? super T, ? extends R> mapper) // Operasi Stream akan memodifikasi data aslinya denga Stream baru. hasil akan di simpan (di memoery heap).. kita buat dari return Entity Contact menjadi DTO ContactResponse
        // <R, A> R collect(Collector<? super T, A, R> collector) // konversi dari Stream<T> ke List<T>
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(contact -> toContactResponse(contact))
                .collect(Collectors.toList());

        // kita tidak mungkin return Page<ContactResponse>.. maka dari itu kita akan menggunakan class PageImpl<T> Implementasi dari Page<T>,
        // PageImpl(List<T> content, Pageable pageable, long total)
        // List<T> content // hasil dari Page<T> ke List<T> ke Stream<T>(modifikasi) ke List<T>
        // Pageable pageable // hasil request body (page dan size)
        // long total // hasil jumlah total element dari return Page<Contact> query DB
        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements()); // PageImpl(List<T> content, Pageable pageable, long total)

    }

}
