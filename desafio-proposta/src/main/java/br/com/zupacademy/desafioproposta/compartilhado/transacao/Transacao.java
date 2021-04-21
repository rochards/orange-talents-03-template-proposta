package br.com.zupacademy.desafioproposta.compartilhado.transacao;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Component
public class Transacao {

    private final EntityManager em;

    public Transacao(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public <T> T salvaEComita(T object) {
        em.persist(object);
        return object;
    }

    @Transactional
    public <T> T atualizaEComita(T object) {
        return em.merge(object);
    }

    public <T> T busca(Class<T> clazz, String field, String value) {
        try {
            return em.createQuery(String.format("FROM %s WHERE %s=:value", clazz.getName(), field), clazz)
                    .setParameter("value", value).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
