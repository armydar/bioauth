package com.bioauth.bioauth.User.model;

import lombok.*;

import javax.persistence.*;

import java.io.Serializable;

@Entity
@EqualsAndHashCode @NoArgsConstructor @Getter @Setter @ToString
public class Biometric implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -96241931067814639L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	private Long user_id;
	
	@Column(columnDefinition = "LONGBLOB")
    private byte[] biometric;
}
