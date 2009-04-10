
package ua.gradsoft.jungle.persistence.cluster_keys;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotations, which used to specify for JPA entity,
 * that id-s are keys wich generated from
 * sequence with name <code> sequenceName </code>
 *
 * We use this annotation instead vendor-specific id generators.
 *
 *
 *<pre>
 * @Entity()
 * public class X
 * {
 *    @Id()
 *    @Column(name=id, precision=40, scale=0)
 *    @SequenceKey(sequenceName="x_seq", type=KeyType.CLUSTER)
 *    public BigDecimal getId()
 *    ...........
 *
 * }
 *</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented()
public @interface SequenceKey {

    /**
     * Name of sequence. (in case of relation database which support sequence -
     * name of database sequence, for non-relation database store - name of sequence
     * in sequence emulation layer).
     * @return sequence name
     */
    public String sequenceName();

    /**
     * Stroe type of key.
     * @return type of key.
     */
    public KeyType type();


}
