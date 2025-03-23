package com.periodic.backend.domain.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.periodic.backend.util.constant.Role;
import com.periodic.backend.util.constant.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private String password;
	private String email;
	private String name;
	private String avatar;
	@JsonIgnore
	@Column(columnDefinition = "TEXT")
	private String refreshToken;
	@Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<FavoriteElement> favoriteElements = new HashSet<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<LearnedElement> learnedElements = new HashSet<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<FavoritePodcast> favoritePodcasts = new HashSet<>();
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<LearnedPodcast> learnedPodcasts = new HashSet<>();
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	System.out.println("ROLE: " + role.name());
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
	
	@Override
	public boolean isAccountNonLocked() {
		return super.isActive();
	}

	@Override
	public String getUsername() {
		return this.email;
	}
	
	@Override
	public boolean isEnabled() {
		return super.isActive();
	}
}
