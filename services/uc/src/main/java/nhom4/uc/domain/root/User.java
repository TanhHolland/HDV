package nhom4.uc.domain.root;

import lombok.Data;
import nhom4.uc.domain.Type;

import jakarta.persistence.*;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 64)
    private String userName;
    @Column(length = 64, nullable = false)
    private String mobile;
    @Column(length = 64)
    private String province;
    @Column(length = 64)
    private String city;
    @Column(length = 64)
    private String district;
    private String street;
    private String originAddress;


    @Enumerated(value = STRING)
    @Column(length = 32, nullable = false)
    private Type type;

}
